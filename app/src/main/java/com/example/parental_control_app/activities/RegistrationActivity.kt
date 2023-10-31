@file:Suppress("DEPRECATION")

package com.example.parental_control_app.activities

import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.R
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.RegistrationScreen
import com.example.parental_control_app.viewmodels.RegistrationViewModel
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var request: BeginSignInRequest
    private lateinit var googleSignInClient : GoogleSignInClient
    private val usersRepository = UsersRepository()
    private var showOneTapUI = false
    private var usedOneTap = false

    companion object {
        private const val RC_SIGN_IN = 9002
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) startStartupActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val toastHelper = ToastHelper(this)
        val registrationViewModel = RegistrationViewModel(toastHelper)
        registrationViewModel.setSignUpCallback { startStartupActivity() }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.your_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions)

        oneTapClient = Identity.getSignInClient(this)
        request = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(false)
            .build()

        setContent {
            ParentalControlAppTheme {
                RegistrationScreen(
                    registrationViewModel,
                    { startLoginActivity() },
//                    { signUpWithGoogle() }
                )
            }
        }
    }

    private fun startStartupActivity() {
        val startupActivity = Intent(this, StartupActivity::class.java)
        startActivity(startupActivity)
        finish()
    }

    private fun displayOneTapUI() {
        oneTapClient.beginSignIn(request)
            .addOnSuccessListener(this) { result ->
                try {
                    usedOneTap = true
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, RC_SIGN_IN,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("ONE TAP UI", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Log.d("ONE TAP UI", e.localizedMessage as String)
                usedOneTap = false
                showOneTapUI = false
                startLegacyGoogleSignUp()
            }
    }

    private fun startLegacyGoogleSignUp() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun signUpWithGoogle() {
        displayOneTapUI()
    }

    private fun startLoginActivity() {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val newUser = UserState(
                        userId = auth.uid.toString(),
                        email = user?.email.toString(),
                    )

                    lifecycleScope.launch {
                        val msg = usersRepository.createUser(newUser)
                        Toast.makeText(this@RegistrationActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StartupActivity::class.java))
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun emailIsUsed(email: String) : Boolean {
        val result = CompletableDeferred<Boolean>()

        lifecycleScope.launch {
            val response = usersRepository.findUserByEmail(email)
            if (response.status == ResponseStatus.SUCCESS) {
                if (response.data != null) {
                    result.complete(response.data["used"] as Boolean)
                }
            }
        }

        return result.await()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                try {
                    if (usedOneTap) {
                        val credential = oneTapClient.getSignInCredentialFromIntent(data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
//                                Log.d("GOOGLE SIGN IN ONE TAP", "Got ID token.")
//                                Log.w("GOOGLE SIGN IN ONE TAP", "$idToken ${credential.id}")
                                lifecycleScope.launch {
                                    if (emailIsUsed(credential.id)) {
                                        Toast.makeText(this@RegistrationActivity, "Email is already used.", Toast.LENGTH_SHORT).show()
//                                        Log.w("GOOGLE SIGN IN ONE TAP", "EMAIL IS USED")
                                    } else {
                                        firebaseAuthWithGoogle(idToken)
//                                        Log.w("GOOGLE SIGN IN ONE TAP", "EMAIL IS AVAILABLE")
                                    }
                                }
                            }
                            else -> {
                                Log.d("GOOGLE SIGN IN", "No ID token!")
                            }
                        }
                    } else {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        val googleSignInAccount = task.result
                        when {
                            googleSignInAccount.idToken != null -> {
                                Log.d("GOOGLE SIGN IN LEGACY", "Got ID token.")
                                Log.w("GOOGLE SIGN IN LEGACY", "${googleSignInAccount.idToken} ${googleSignInAccount.email}")
                                lifecycleScope.launch {
                                    if (emailIsUsed(googleSignInAccount.email.toString())) {
                                        Toast.makeText(this@RegistrationActivity, "Email is already used.", Toast.LENGTH_SHORT).show()
//                                        Log.w("GOOGLE SIGN IN LEGACY", "EMAIL IS USED")
                                    } else {
                                        firebaseAuthWithGoogle(googleSignInAccount.idToken.toString())
//                                        Log.w("GOOGLE SIGN IN LEGACY", "EMAIL IS AVAILABLE")
                                    }
                                }
                            }
                            else -> {
                                Log.d("GOOGLE SIGN IN", "No ID token!")
                            }
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(ContentValues.TAG, "One-tap dialog was closed.")
                            showOneTapUI = false
                        } CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(ContentValues.TAG, "One-tap encountered a network error.")
                        } else -> {
                            Log.d(
                                ContentValues.TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }
}