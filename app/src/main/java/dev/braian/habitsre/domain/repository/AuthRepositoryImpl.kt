package dev.braian.habitsre.domain.repository

//import dev.braian.habitsre.core.OneTapSignInResponse
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import dev.braian.habitsre.core.Constants
import dev.braian.habitsre.core.CredentialResponse
import dev.braian.habitsre.core.FirebaseSignInResponse
import dev.braian.habitsre.core.Response
import dev.braian.habitsre.core.SignOutResponse
import dev.braian.habitsre.domain.model.DataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var credentialManager: CredentialManager,
    @Named(Constants.TAG.GOOGLE_SIGN_IN_REQUEST)
    private var googleIdSignInRequest: GetCredentialRequest,
    @Named(Constants.TAG.GOOGLE_SIGN_UP_REQUEST)
    private var googleIdSignUpRequest: GetCredentialRequest

) : AuthRepository {

    override fun getAuthState(viewModelScope: CoroutineScope) = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser)
            Log.i(
                Constants.TAG.AUTH_REPOSITORY_IMPL,
                "User: ${auth.currentUser?.uid} Not authenticated"
            )
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser)


    override suspend fun signInAnonymously(): FirebaseSignInResponse {
        return try {
            val authResult = auth.signInAnonymously().await()
            authResult?.user?.let { user ->
                Log.i(
                    Constants.TAG.AUTH_REPOSITORY_IMPL,
                    "FirebaseAuthSuccess: Anonymous UID : ${user.uid}"
                )
            }
            Response.Success(true)
            //Response.Success(authResult)
        } catch (error: Exception) {
            Log.i(
                Constants.TAG.AUTH_REPOSITORY_IMPL,
                "FirebaseAuthError: Failed to Sign in anonymously"
            )
            Response.Failure(error)
        }
    }

    override suspend fun signOut(): SignOutResponse {
        return try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    private suspend fun authenticateUser(credential: AuthCredential): FirebaseSignInResponse {
        return if (auth.currentUser != null) {
            // Verifica se o usuário atual é anônimo
            if (auth.currentUser?.isAnonymous == true) {
                try {
                    Log.i(Constants.TAG.AUTH_REPOSITORY_IMPL, "Anonymous user detected, linking account.")
                    // Tenta vincular a conta
                    authLink(credential)
                } catch (e: Exception) {
                    Log.e(Constants.TAG.AUTH_REPOSITORY_IMPL, "Error linking account: ${e.message}")
                    // Retorna uma falha ao tentar vincular
                   Response.Failure(e)
                }
            } else {
                // O usuário já está autenticado, apenas faz o sign in com a credencial
                authSignIn(credential)
            }
        } else {
            // Nenhum usuário autenticado, procede para o sign in
            authSignIn(credential)
        }
    }

    private suspend fun authSignIn(credential: AuthCredential): FirebaseSignInResponse {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            Log.i(Constants.TAG.AUTH_REPOSITORY_IMPL, "User: ${authResult?.user?.uid}")
            DataProvider.updateAuthState(authResult?.user)
            Response.Success(true)
        } catch (error: Exception) {
            Response.Failure(error)
        }
    }

    private suspend fun authLink(credential: AuthCredential): FirebaseSignInResponse {
        return try {
            val authResult = auth.currentUser?.linkWithCredential(credential)?.await()
            Log.i(Constants.TAG.AUTH_REPOSITORY_IMPL, "User linked: ${authResult?.user?.uid}")
            DataProvider.updateAuthState(authResult?.user)
            Response.Success(true)
        } catch (e: FirebaseAuthUserCollisionException) {
            // Handle the account collision by signing in the user
            Log.i(Constants.TAG.AUTH_REPOSITORY_IMPL, "Account already linked, signing in...")
            val signInResult = authSignIn(credential)
            return if (signInResult is Response.Success) {
                Log.i(Constants.TAG.AUTH_REPOSITORY_IMPL, "User signed in after collision: ${auth.currentUser?.uid}")
                Response.Success(true)
            } else {
                signInResult // return the actual failure result if signing in fails
            }
        } catch (error: Exception) {
            Response.Failure(error)
        }
    }


    override suspend fun signInWithGoogle(googleIdToken: String): FirebaseSignInResponse {
        val googleCredential = GoogleAuthProvider
            .getCredential(googleIdToken, null)
        return authenticateUser(googleCredential)
    }


    override suspend fun getGoogleCredentials(context: Context): CredentialResponse {
        return try {
            val signInResponse = credentialManager.getCredential(context, googleIdSignInRequest)
            Response.Success(signInResponse)
        } catch (e: Exception) {
            try {
                val signUpResponse = credentialManager.getCredential(context, googleIdSignUpRequest)
                Response.Success(signUpResponse)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    override suspend fun getGoogleCredentials2(context: Context): CredentialResponse {
        return try {
            val signInResponse = credentialManager.getCredential(context, googleIdSignInRequest)
            handleCredentialSignIn(signInResponse)
            Response.Success(signInResponse)

        } catch (e: Exception) {
            try {
                val signUpResponse = credentialManager.getCredential(context, googleIdSignUpRequest)
                Response.Success(signUpResponse)
            } catch (e: Exception) {
                Response.Failure(e)
            }
        }
    }

    suspend fun handleCredentialSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        signInWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("AuthViewModel", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("AuthViewModel", "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("AuthViewModel", "Unexpected type of credential")
            }
        }
    }

}