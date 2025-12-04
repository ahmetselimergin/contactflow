package com.example.contactflow.auth

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

sealed class AuthResult {
    object Success : AuthResult()
    data class Failure(val exception: Exception?) : AuthResult()
}

@Composable
fun rememberGoogleSignInLauncher(
    onResult: (AuthResult) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("300914565509-isrh2qhee0jsq49dg9uo1i67fs5dimps.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onResult(AuthResult.Success)
                    } else {
                        onResult(AuthResult.Failure(authTask.exception))
                    }
                }
        } catch (e: ApiException) {
            onResult(AuthResult.Failure(e))
        }
    }

    return { launcher.launch(googleSignInClient.signInIntent) }
}


fun handleGoogleAuthResult(
    result: AuthResult,
    navController: NavController,
    context: Context
) {
    when (result) {
        is AuthResult.Success -> {
            navController.navigate("eventList") {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        is AuthResult.Failure -> {
            val message = when (result.exception) {
                is ApiException -> "Google sign in failed: ${result.exception.statusCode}"
                else -> result.exception?.message ?: "Authentication failed."
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
