package com.example.parental_control_app.googleauth

import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.parental_control_app.MainActivity
import com.example.parental_control_app.R
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

class GoogleOAuthActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var request: BeginSignInRequest
    private lateinit var auth: FirebaseAuth
    private var showOneTapUI = true
    private lateinit var googleSignInClient : GoogleSignInClient
    companion object {
        const val RC_SIGN_IN = 9001
        const val REQ_ONE_TAP = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.your_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        oneTapClient = Identity.getSignInClient(this)
        request = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true)
            .build()

        displayOneTapUI()
    }
    private fun displayOneTapUI() {
        oneTapClient.beginSignIn(request)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Log.d(ContentValues.TAG, e.localizedMessage as String)
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            Log.d(ContentValues.TAG, "Got ID token.")
                            firebaseAuthWithGoogle(idToken)
                        } else -> {
                            Log.d(ContentValues.TAG, "No ID token!")
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
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val idToken = task.result.idToken
                when {
                    idToken != null -> {
                        Log.d(ContentValues.TAG, "Got ID token.")
                        firebaseAuthWithGoogle(idToken)
                    } else -> {
                    Log.d(ContentValues.TAG, "No ID token!")
                }
                }
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName} - ${user?.uid}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}