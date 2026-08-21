package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.min

@Composable
fun VirtualJoystick(
    onVectorChange: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxRadiusPx = with(LocalDensity.current) { 55.dp.toPx() }
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .border(2.dp, HoneyGold.copy(alpha = 0.6f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = delta.getDistance()
                        val clamped = if (dist > maxRadiusPx) delta * (maxRadiusPx / dist) else delta
                        knobOffset = clamped
                        onVectorChange(clamped / maxRadiusPx)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxRadiusPx) newOffset * (maxRadiusPx / dist) else newOffset
                        knobOffset = clamped
                        onVectorChange(clamped / maxRadiusPx)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onVectorChange(Offset.Zero)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onVectorChange(Offset.Zero)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.toInt(), knobOffset.y.toInt()) }
                .size(54.dp)
                .clip(CircleShape)
                .background(HoneyGold)
                .border(2.dp, HoneyAmber, CircleShape)
        )
    }
}

@Composable
fun ActionControls(
    isNearHive: Boolean,
    isConverting: Boolean,
    onGatherClick: () -> Unit,
    onHiveOpenClick: () -> Unit,
    onConvertClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (isNearHive) {
            // Convert Honey Action Button
            Button(
                onClick = onConvertClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConverting) Color(0xFF43A047) else HoneyAmber
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(72.dp)
                    .testTag("btn_convert_honey")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🍯", fontSize = 20.sp)
                    Text(
                        text = if (isConverting) "Stop" else "Convert",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Open Hive Interface Button
            Button(
                onClick = onHiveOpenClick,
                colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                shape = CircleShape,
                modifier = Modifier
                    .size(76.dp)
                    .testTag("btn_open_hive")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏰", fontSize = 20.sp)
                    Text(
                        text = "Hive",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Scoop / Gather Action Button
        Button(
            onClick = onGatherClick,
            colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
            shape = CircleShape,
            modifier = Modifier
                .size(88.dp)
                .testTag("btn_gather_scoop")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🥄", fontSize = 26.sp)
                Text(
                    text = "SCOOP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}
