package dev.braian.habitsre.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseUser
import dev.braian.habitsre.core.CredentialResponse
import dev.braian.habitsre.core.FirebaseSignInResponse
import dev.braian.habitsre.core.Response
import dev.braian.habitsre.core.SignOutResponse

enum class AuthState{
    Authenticated,
    SignedIn,
    SignedOut
}
object DataProvider {

    var anonymousSignInResponse by mutableStateOf<FirebaseSignInResponse>(Response.Success(null))
    var googleSignInResponse by mutableStateOf<FirebaseSignInResponse>(Response.Success(null))
    var signOutResponse by mutableStateOf<SignOutResponse>(Response.Success(false))

    var user by mutableStateOf<FirebaseUser?>(null)

    var isAuthenticated by mutableStateOf(false)

    var isAnonymous by mutableStateOf(false)

    var credentialResponse by mutableStateOf<CredentialResponse>(Response.Success(null))

    var authState by mutableStateOf(AuthState.SignedOut)
        private set

    fun updateAuthState(user: FirebaseUser?) {
        this.user = user
        isAuthenticated = user != null
        isAnonymous = user?.isAnonymous ?: false

        authState = if (isAuthenticated) {
            if (isAnonymous) AuthState.Authenticated else AuthState.SignedIn
        } else {
            AuthState.SignedOut
        }
    }
}