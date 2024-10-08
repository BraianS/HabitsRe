package dev.braian.habitsre.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin


fun createWavePath(
    progressWidth: Float,
    canvasHeight: Float,
    waveAmplitude: Float,
    waveFrequency: Float,
    waveSegments: Int,
    waveOffset: Float
): Path {
    return Path().apply {
        moveTo(0f, 0f)
        lineTo(progressWidth, 0f)

        for (i in 0..canvasHeight.toInt() step waveSegments) {
            val x1 = progressWidth - waveAmplitude
            val x2 = progressWidth + waveAmplitude
            val y = i.toFloat()

            cubicTo(
                x1, y + waveAmplitude * sin((i / waveFrequency) + waveOffset),
                x2, y + waveSegments / 2f + waveAmplitude * sin((i / waveFrequency) + waveOffset),
                progressWidth, y + waveSegments
            )
        }

        lineTo(progressWidth, canvasHeight)
        lineTo(0f, canvasHeight)
        close()
    }
}

@Composable
@Preview
fun IoasysLogo(logoSize: Dp = 150.dp) {

    val gradientColors = listOf(Color(0xFF522D8D), Color(0xFF000000), Color(0xFF522D8D))

    val externalArcSize = LocalDensity.current.run { logoSize.toPx() }
    val innerCircleSize = (externalArcSize / 1.8f)
    val strokeThickness = externalArcSize * 0.1f
    val dotSize = externalArcSize * 0.12f
    val spacingBetween = externalArcSize * 0.1f

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            size = size, brush = Brush.linearGradient(colors = gradientColors, start = Offset.Zero)
        )

        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(
                x = (center.x - (externalArcSize / 2)),
                y = center.y - (externalArcSize / 2)
            ),
            size = Size(externalArcSize, externalArcSize), style = Stroke(
                cap = StrokeCap.Round,
                width = strokeThickness
            )
        )

        drawCircle(
            color = Color.White, center = center, radius = (innerCircleSize / 2), style = Stroke(
                cap = StrokeCap.Round, width = strokeThickness
            )
        )

        drawCircle(
            color = Color.White, center = Offset(
                ((center.x - (externalArcSize / 2))),
                ((center.y - dotSize) - spacingBetween)
            ), radius = (dotSize / 2f)
        )


    }

}

@Composable
@Preview
fun canvasRectWave() {

    val infiniteTransition = rememberInfiniteTransition()

    // Anima o ponto intermediário da primeira curva para frente e para trás
    val animatedOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Anima o ponto intermediário da segunda curva para frente e para trás
    val animatedOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    // Datas fictícias e diferença entre elas
    val datePassEpoch = 1726064330 // Um timestamp de exemplo
    val dateNow = System.currentTimeMillis() // Pega o timestamp atual
    val differenceBetweenDate = datePassEpoch - dateNow // Calcula a diferença entre as duas datas

    // Um cartão para envolver o canvas com dimensões específicas
    Card(
        modifier = Modifier
            .fillMaxWidth() // O cartão vai preencher toda a largura da tela
            .padding(8.dp) // Margem de 8dp ao redor do cartão
            .height(150.dp) // Altura do cartão é 150dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) { // Um Box para preencher todo o tamanho do cartão

            // O Canvas é o lugar onde o desenho personalizado será feito
            Canvas(
                modifier = Modifier
                    .fillMaxSize() // O Canvas vai preencher todo o espaço disponível
            ) {
                // Obtém a altura e a largura do Canvas
                val height = size.height // Altura do Canvas (ajusta ao tamanho do cartão)
                val width = size.width // Largura do Canvas (ajusta ao tamanho do cartão)

                val pathBezie = Path().apply {
                    // Movendo para o ponto inicial no canto superior esquerdo
                    moveTo(0f, 0f)

                    // Primeira curva (ponto intermediário no topo central)
                    quadraticBezierTo(
                        50f+ animatedOffset1, 40f - animatedOffset2,  // Ponto de controle
                        100f - animatedOffset2, 100f // Ponto de ancoragem
                    )

                    // Terceira curva, indo para a parte inferior esquerda
                    quadraticBezierTo(
                        width.times(0.25f) + animatedOffset2, height.times(0.75f), // Ponto de controle na parte inferior direita
                        0f, height // Ponto final, voltando ao canto inferior esquerdo
                    )
                }


                // Desenha o caminho usando a cor vermelha
                drawPath(color = Color.Red, path = pathBezie, style = Stroke(width = 5f))
            }
        }
    }
}