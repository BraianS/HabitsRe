package dev.braian.habitsre.core

import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

typealias FirebaseSignInResponse = Response<Boolean>
typealias SignOutResponse = Response<Boolean>
typealias AuthStateResponse = StateFlow<FirebaseUser?>
typealias CredentialResponse = Response<GetCredentialResponse>


sealed class Response<out T> {
    object Loading: Response<Nothing>()
    data class Success<out T>(val data: T?): Response<T>()
    data class Failure(val e: Exception): Response<Nothing>()
}