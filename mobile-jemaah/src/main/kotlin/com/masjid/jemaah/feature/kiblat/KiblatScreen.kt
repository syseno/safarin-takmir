package com.masjid.jemaah.feature.kiblat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiblatScreen(
    onNavigateBack: () -> Unit,
    viewModel: KiblatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Arah Kiblat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                KiblatCompass(
                    qiblaDirection = state.qiblaDirection,
                    currentHeading = state.currentHeading
                )
            }
        }
    }
}

@Composable
fun KiblatCompass(qiblaDirection: Float, currentHeading: Float) {
    val animatedHeading by animateFloatAsState(targetValue = currentHeading, label = "heading")
    
    // Relative direction: Qibla is at qiblaDirection, device is at currentHeading
    // So the needle should point to (qiblaDirection - currentHeading)
    val relativeDirection = (qiblaDirection - animatedHeading + 360) % 360

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ka'bah di ${qiblaDirection.toInt()}°",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(300.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2
                
                // Draw circle
                drawCircle(
                    color = Color.LightGray,
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                )

                // Draw N, E, S, W
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                
                rotate(-animatedHeading, center) {
                    val paintN = android.graphics.Paint(textPaint).apply { 
                        color = android.graphics.Color.RED 
                        isFakeBoldText = true
                    }
                    drawContext.canvas.nativeCanvas.drawText("N", center.x, center.y - radius + 50f, paintN)
                    drawContext.canvas.nativeCanvas.drawText("S", center.x, center.y + radius - 20f, textPaint)
                    drawContext.canvas.nativeCanvas.drawText("E", center.x + radius - 40f, center.y + 15f, textPaint)
                    drawContext.canvas.nativeCanvas.drawText("W", center.x - radius + 40f, center.y + 15f, textPaint)
                }

                // Draw Qibla Needle (Arrow)
                rotate(relativeDirection, center) {
                    // Needle body (Thin line for the rest)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = center,
                        end = Offset(center.x, center.y + radius - 40.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                    
                    // Main Needle (Pointing to Qibla)
                    drawLine(
                        color = Color(0xFFC62828), // Strong Red
                        start = center,
                        end = Offset(center.x, center.y - radius + 30.dp.toPx()),
                        strokeWidth = 6.dp.toPx()
                    )
                    
                    // Arrow head
                    val arrowSize = 15.dp.toPx()
                    val tip = Offset(center.x, center.y - radius + 25.dp.toPx())
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(tip.x, tip.y)
                            lineTo(tip.x - arrowSize/2, tip.y + arrowSize)
                            lineTo(tip.x + arrowSize/2, tip.y + arrowSize)
                            close()
                        },
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Hadapkan perangkat ke arah jarum merah",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Jauhkan dari benda logam atau magnet",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )
    }
}
