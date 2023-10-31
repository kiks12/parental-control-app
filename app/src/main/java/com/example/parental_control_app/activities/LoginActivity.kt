@file:Suppress("DEPRECATION")

package com.example.parental_control_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.parental_control_app.R
import com.example.parental_control_app.data.ResponseStatus
import com.example.parental_control_app.helpers.ToastHelper
import com.example.parental_control_app.repositories.users.UserState
import com.example.parental_control_app.repositories.users.UsersRepository
import com.example.parental_control_app.screens.LoginScreen
import com.example.parental_control_app.ui.theme.ParentalControlAppTheme
import com.example.parental_control_app.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private var loading = mutableStateOf(false)

    private val usersRepository = UsersRepository()

    companion object {
        private const val REQ_LEGACY = 9002
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
        val loginViewModel = LoginViewModel(toastHelper)
        loginViewModel.setSignInCallback { startStartupActivity() }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.your_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, googleSignInOptions)
        googleSignInClient.signOut()

        setContent {
            ParentalControlAppTheme {
                if (loading.value) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LoginScreen(
                        loginViewModel,
                        signInWithGoogle = { signInWithGoogle() },
                        startRegistrationActivity = { startRegistrationActivity() }
                    )
                }
            }
        }
    }

    private fun startStartupActivity() {
        val startupActivity = Intent(this, StartupActivity::class.java)
        startActivity(startupActivity)
        finish()
    }

    private fun signInWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, REQ_LEGACY)
    }

    private fun startRegistrationActivity() {
        val registrationActivity = Intent(this, RegistrationActivity::class.java)
        startActivity(registrationActivity)
        finish()
    }

    private fun saveNewGoogleAccountThenFirebaseAuth(idToken: String) {
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
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StartupActivity::class.java))
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }


    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

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
            REQ_LEGACY -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val googleSignInAccount = task.result
                    Log.w("GOOGLE SIGN IN", googleSignInAccount.email + " " + googleSignInAccount.idToken)

                    lifecycleScope.launch(Dispatchers.Default){
                        withContext(Dispatchers.Main) {
                            loading.value  = true
                        }

                        when {
                            googleSignInAccount.idToken != null && task.isSuccessful -> {
                                Log.w("GOOGLE SIGN IN", emailIsUsed(googleSignInAccount.email.toString()).toString())
                                if (emailIsUsed(googleSignInAccount.email.toString())) {
                                    firebaseAuthWithGoogle(googleSignInAccount.idToken.toString())
                                } else {
                                    saveNewGoogleAccountThenFirebaseAuth(googleSignInAccount.idToken.toString())
                                }
                            }
                            else -> {
                                Log.d("GOOGLE SIGN IN", "No ID token!")
                            }
                        }
                    }
                } catch (e:ApiException) {
                    e.localizedMessage?.let { Log.w("GOOGLE SIGN IN ERROR", it) }
                }
            }
        }
    }
}