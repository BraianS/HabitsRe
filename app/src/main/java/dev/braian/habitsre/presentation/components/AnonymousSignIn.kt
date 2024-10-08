package dev.braian.habitsre.presentation.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.braian.habitsre.core.Response
import dev.braian.habitsre.domain.model.DataProvider
import dev.braian.habitsre.presentation.screen.AuthLoginProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun AnonymousSignIn() {
    when (val anonymousResponse = DataProvider.anonymousSignInResponse) {
        is Response.Loading -> {
            Log.i("Login:AnonymousSignIn", "Loading")
             AuthLoginProgressIndicator()
        }

        is Response.Success -> anonymousResponse.data?.let { authResult ->
            Log.i("Login:AnonymousSignIn", "Success: $authResult")

        }

        is Response.Failure -> LaunchedEffect(Unit) {
            launch {  }
            Log.e("Login:AnonymousSignIn", "${anonymousResponse.e}")
        }
    }
}