package dev.braian.habitsre.domain.repository


import android.content.Context
import dev.braian.habitsre.core.AuthStateResponse
import dev.braian.habitsre.core.CredentialResponse
import dev.braian.habitsre.core.FirebaseSignInResponse
import dev.braian.habitsre.core.SignOutResponse
import kotlinx.coroutines.CoroutineScope

interface AuthRepository {

    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse

    suspend fun signInAnonymously(): FirebaseSignInResponse

    suspend fun signOut(): SignOutResponse

    suspend fun getGoogleCredentials(context: Context): CredentialResponse

    suspend fun signInWithGoogle(googleIdToken: String): FirebaseSignInResponse

    suspend fun getGoogleCredentials2(context: Context): CredentialResponse

}