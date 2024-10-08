package dev.braian.habitsre.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.braian.habitsre.R
import dev.braian.habitsre.domain.model.AuthState
import dev.braian.habitsre.domain.model.DataProvider
import dev.braian.habitsre.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val textStyle = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    )
    val composableScope = rememberCoroutineScope()

    val authState = DataProvider.authState

    Box(modifier = Modifier.fillMaxSize()) {
        // Show profile content only when the user is signed in
        if (authState == AuthState.SignedIn || authState == AuthState.Authenticated) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                authViewModel.currentUser?.let { user ->
                    val user = user.value
                    if (user!!.isAnonymous) {
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp)
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Black
                                )
                                .clip(RoundedCornerShape(4.dp)),
                            model = ImageRequest.Builder(LocalContext.current).data(R.drawable.logo)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        user.photoUrl?.let {
                            AsyncImage(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                model = ImageRequest.Builder(LocalContext.current).data(it)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile picture",
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = authViewModel.getUserName(),
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        user.email?.let { mailId ->
                            Text(
                                text = "E-mail: $mailId",
                                style = textStyle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            // Show the LoginScreen when the user is not signed in
            LoginScreen(authViewModel = authViewModel)
        }

        Button(
            onClick = {
                if (authState == AuthState.SignedIn || authState == AuthState.Authenticated) {
                    composableScope.launch {
                        authViewModel.signOut()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_logout_24),
                    contentDescription = "Logout",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = if (authState == AuthState.SignedIn || authState == AuthState.Authenticated)
                        "Sign out"
                    else
                        "Sign-in",
                    modifier = Modifier.padding(6.dp),
                )
            }
        }
    }
}
