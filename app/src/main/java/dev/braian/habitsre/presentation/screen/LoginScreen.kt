package dev.braian.habitsre.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.braian.habitsre.R
import dev.braian.habitsre.domain.model.AuthState
import dev.braian.habitsre.domain.model.DataProvider
import dev.braian.habitsre.presentation.components.AnonymousSignIn
import dev.braian.habitsre.presentation.components.GoogleSignIn
import dev.braian.habitsre.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel
) {

    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(80.dp),
                imageVector = Icons.Default.Lock,
                contentDescription = "Icon Lock"
            )
            Spacer(modifier = Modifier.size(32.dp))

            Button(
                onClick = {
                    composableScope.launch {
                       authViewModel.getGoogleCredentials2(context = context)
                    }
                },
                modifier = Modifier
                    .size(width = 200.dp, height = 50.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Sign in with Google"
                )
                Text(
                    text = "Sign in with Google",
                    modifier = Modifier.padding(6.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
            if (DataProvider.authState == AuthState.SignedOut) {

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        authViewModel.signInAnonymously()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .size(width = 200.dp, height = 50.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "Skip",
                        modifier = Modifier.padding(6.dp),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }

   AnonymousSignIn ()
    GoogleSignIn()
}
