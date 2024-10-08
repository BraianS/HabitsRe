package dev.braian.habitsre.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.braian.habitsre.core.Response
import dev.braian.habitsre.domain.model.DataProvider
import dev.braian.habitsre.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    val oneTapClient: SignInClient
) : ViewModel() {

    val currentUser = getAuthState()

    init {
        getAuthState()
    }

    fun getUserName(): String {

        // Verifica se o usuário é anônimo e retorna o nome apropriado
        return if (currentUser?.value?.isAnonymous == true) {
            "Anonymous"
        } else {
            currentUser?.value?.displayName ?: "No Name"
        }
    }

    private fun getAuthState() = repository.getAuthState(viewModelScope)

    fun signInAnonymously() = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.anonymousSignInResponse = Response.Loading
        DataProvider.anonymousSignInResponse = repository.signInAnonymously()
    }

    fun signOut() = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.signOutResponse = Response.Loading
        DataProvider.signOutResponse = repository.signOut()

    }

    suspend fun getGoogleCredentials(context: Context) = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.credentialResponse = Response.Loading
        DataProvider.credentialResponse = repository.getGoogleCredentials(context)
    }

    fun handleCredentialSignIn(result: GetCredentialResponse) {
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

    private fun signInWithGoogle(googleIdToken: String) = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.googleSignInResponse = Response.Loading
        DataProvider.googleSignInResponse = repository.signInWithGoogle(googleIdToken)
    }

    suspend fun getGoogleCredentials2(context: Context) = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.credentialResponse = Response.Loading
        DataProvider.credentialResponse = repository.getGoogleCredentials2(context)
    }


}