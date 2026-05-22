package com.masjid.jemaah.feature.kiblat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.jemaah.ui.theme.IslamicGreen
import com.masjid.jemaah.ui.theme.IslamicGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiblatScreen(
    onNavigateBack: () -> Unit,
    viewModel: KiblatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text("Arah Kiblat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Decoration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(IslamicGreenDark, Color.Transparent)
                        )
                    )
            )

            if (state.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                KiblatCompassSection(
                    qiblaDirection = state.qiblaDirection,
                    currentHeading = state.currentHeading,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun KiblatCompassSection(qiblaDirection: Float, currentHeading: Float, modifier: Modifier = Modifier) {
    val animatedHeading by animateFloatAsState(targetValue = currentHeading, label = "heading")
    
    // Relative direction: Qibla is at qiblaDirection, device is at currentHeading
    val relativeDirection = (qiblaDirection - animatedHeading + 360) % 360

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Posisi Ka'bah: ${qiblaDirection.toInt()}° dari Utara",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val primaryColor = MaterialTheme.colorScheme.primary
        val surfaceColor = MaterialTheme.colorScheme.surface

        Box(contentAlignment = Alignment.Center) {
            // Outer Ring Glow
            Surface(
                modifier = Modifier.size(310.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ) {}

            Canvas(modifier = Modifier.size(300.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2
                
                // Draw circle border
                drawCircle(
                    color = primaryColor.copy(alpha = 0.2f),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx())
                )
                
                drawCircle(
                    color = surfaceColor,
                    radius = radius - 4.dp.toPx(),
                    center = center
                )

                // Draw compass markings
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(
                        (onSurfaceColor.alpha * 255).toInt(),
                        (onSurfaceColor.red * 255).toInt(),
                        (onSurfaceColor.green * 255).toInt(),
                        (onSurfaceColor.blue * 255).toInt()
                    )
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                
                rotate(-animatedHeading, center) {
                    val paintN = android.graphics.Paint(textPaint).apply { 
                        color = android.graphics.Color.argb(
                            (primaryColor.alpha * 255).toInt(),
                            (primaryColor.red * 255).toInt(),
                            (primaryColor.green * 255).toInt(),
                            (primaryColor.blue * 255).toInt()
                        )
                    }
                    drawContext.canvas.nativeCanvas.drawText("U", center.x, center.y - radius + 60f, paintN)
                    drawContext.canvas.nativeCanvas.drawText("S", center.x, center.y + radius - 30f, textPaint)
                    drawContext.canvas.nativeCanvas.drawText("T", center.x + radius - 50f, center.y + 15f, textPaint)
                    drawContext.canvas.nativeCanvas.drawText("B", center.x - radius + 50f, center.y + 15f, textPaint)
                }

                // Draw Qibla Needle
                rotate(relativeDirection, center) {
                    // Needle body
                    drawLine(
                        color = onSurfaceColor.copy(alpha = 0.1f),
                        start = center,
                        end = Offset(center.x, center.y + radius - 60.dp.toPx()),
                        strokeWidth = 4.dp.toPx()
                    )
                    
                    // Main Needle (Pointing to Qibla)
                    drawLine(
                        color = primaryColor,
                        start = center,
                        end = Offset(center.x, center.y - radius + 40.dp.toPx()),
                        strokeWidth = 8.dp.toPx()
                    )
                    
                    // Arrow head (The triangle pointing to Kaaba)
                    val arrowSize = 24.dp.toPx()
                    val tip = Offset(center.x, center.y - radius + 35.dp.toPx())
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(tip.x, tip.y)
                            lineTo(tip.x - arrowSize/2, tip.y + arrowSize)
                            lineTo(tip.x + arrowSize/2, tip.y + arrowSize)
                            close()
                        },
                        color = primaryColor
                    )
                    
                    // Small Center Point
                    drawCircle(color = IslamicGreenDark, radius = 6.dp.toPx(), center = center)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Arahkan perangkat hingga jarum hijau menunjuk ke atas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hindari benda logam untuk akurasi lebih baik.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
