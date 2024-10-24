package dev.braian.habitsre.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.braian.habitsre.core.Screen
import dev.braian.habitsre.domain.model.OnBoardingPage
import dev.braian.habitsre.viewmodel.AuthViewModel
import kotlin.math.absoluteValue

@Composable
fun WelcomeScreen(
    authViewModel: AuthViewModel

) {
    val pagerState = rememberPagerState { 4 }

    val pages = listOf(
        OnBoardingPage.Welcome,
        OnBoardingPage.Presentation,
        OnBoardingPage.Finish,
        OnBoardingPage.Login
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier

        ) { page ->
            if(pagerState.currentPage == pages.lastIndex) {
                LoginScreen(authViewModel = authViewModel)

            } else {
                PageScreen(onBoardingPage = pages[page])
            }
        }

        // HorizontalPagerIndicator below the pager
        HorizontalPagerIndicator(
            pageCount = pages.size,
            currentPage = pagerState.currentPage,
            targetPage = pagerState.targetPage,
            currentPageOffsetFraction = pagerState.currentPageOffsetFraction,
            modifier = Modifier
                .padding(vertical = 16.dp) // Add vertical padding to create space
                .align(Alignment.CenterHorizontally) // Center the indicator horizontally
        )
    }
}

@Composable
private fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    targetPage: Int,
    currentPageOffsetFraction: Float,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.DarkGray,
    unselectedIndicatorSize: Dp = 8.dp,
    selectedIndicatorSize: Dp = 8.dp,
    indicatorCornerRadius: Dp = 2.dp,
    indicatorPadding: Dp = 2.dp,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth() // Center the indicator across the screen
            .height(selectedIndicatorSize + indicatorPadding * 2)
    ) {
        repeat(pageCount) { page ->
            val (color, size) =
                if (currentPage == page || targetPage == page) {
                    val pageOffset =
                        ((currentPage - page) + currentPageOffsetFraction).absoluteValue

                    val offsetPercentage = 1f - pageOffset.coerceIn(0f, 1f)

                    val size =
                        unselectedIndicatorSize + ((selectedIndicatorSize - unselectedIndicatorSize) * offsetPercentage)

                    indicatorColor.copy(alpha = offsetPercentage) to size
                } else {
                    indicatorColor.copy(alpha = 0.1f) to unselectedIndicatorSize
                }

            Box(
                modifier = Modifier
                    .padding(horizontal = indicatorPadding)
                    .clip(RoundedCornerShape(indicatorCornerRadius))
                    .background(color)
                    .width(size)
                    .height(size / 2)
            )
        }
    }
}


@Composable
fun FinishButton(
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier.padding(horizontal = 40.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {

        AnimatedVisibility(
            visible = pagerState.currentPage == 2,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onClick, colors =  ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )) {
              Text(text = "Finish")
            }
        }
    }

}

@Composable
fun PageScreen(onBoardingPage: OnBoardingPage) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.7f),
            painter = painterResource(id = onBoardingPage.image),
            contentDescription = "Page image"
        )
        Text(
            text = onBoardingPage.title,
            modifier = Modifier.fillMaxWidth(),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = onBoardingPage.description,
            modifier = Modifier.fillMaxWidth(),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
@Preview
fun WelcomePageScreen() {
    PageScreen(onBoardingPage = OnBoardingPage.Welcome)
}

@Composable
@Preview
fun PresentationPageScreen() {
    PageScreen(onBoardingPage = OnBoardingPage.Presentation)
}

@Composable
@Preview
fun FinishPageScreen() {
    PageScreen(onBoardingPage = OnBoardingPage.Finish)
}

