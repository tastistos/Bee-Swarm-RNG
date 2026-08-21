package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.GameAudio
import com.example.game.GameState
import com.example.model.Field
import com.example.ui.theme.*

@Composable
fun TopHud(
    gameState: GameState,
    currentField: Field?,
    onSkipTutorial: () -> Unit,
    onOpenRngRoll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMuted by remember { mutableStateOf(GameAudio.isMuted()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Main Resource Counters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Honey & Pollen Stats
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Honey Jar Counter
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GameSurfaceDark.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, HoneyGold),
                    modifier = Modifier.height(38.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🍯", fontSize = 18.sp)
                        Text(
                            text = formatNumber(gameState.honey),
                            fontWeight = FontWeight.Bold,
                            color = HoneyGold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Pollen Capacity Meter
                val pollenRatio = (gameState.pollen.toFloat() / gameState.maxPollenCapacity.coerceAtLeast(1L)).coerceIn(0f, 1f)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GameSurfaceDark.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PollenYellow),
                    modifier = Modifier.height(38.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🌼", fontSize = 18.sp)
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "${formatNumber(gameState.pollen)} / ${formatNumber(gameState.maxPollenCapacity)}",
                                fontWeight = FontWeight.Bold,
                                color = PollenYellow,
                                fontSize = 12.sp
                            )
                            LinearProgressIndicator(
                                progress = { pollenRatio },
                                modifier = Modifier
                                    .width(85.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PollenYellow,
                                trackColor = GameSurfaceElevated
                            )
                        }
                    }
                }

                // Field Badge
                if (currentField != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = currentField.groundColor.copy(alpha = 0.9f),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentField.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Right: RNG Egg Hatch Shortcut & Swarm Info & Audio Toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Hatch RNG Egg Button
                Button(
                    onClick = onOpenRngRoll,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp).testTag("btn_quick_hatch")
                ) {
                    Text(text = "🥚 HATCH (RNG)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }

                // Swarm Count
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GameSurfaceDark.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HoneyAmber),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🐝", fontSize = 16.sp)
                        Text(
                            text = "${gameState.equippedBees.size}/${gameState.hiveSlots} Bees",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = HoneyCream
                        )
                    }
                }

                // Audio Mute Toggle
                IconButton(
                    onClick = {
                        isMuted = GameAudio.toggleMute()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GameSurfaceDark.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Audio",
                        tint = HoneyGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Tutorial Guidance Banner (if active)
        if (!gameState.isTutorialCompleted && gameState.tutorialStep <= 7) {
            val stepText = when (gameState.tutorialStep) {
                1 -> "🎯 Step 1: Use joystick to walk to the Dandelion Field."
                2 -> "🎯 Step 2: Tap [SCOOP] to gather pollen until backpack has 30+ pollen."
                3 -> "🎯 Step 3: Walk back to Hive and tap [Convert] to make Honey!"
                4 -> "🎯 Step 4: Stand in Hive and tap [Hive] to open hive manager."
                5 -> "🎯 Step 5: Select your Basic Bee and feed it a Treat (+10 Bond)."
                6 -> "🎯 Step 6: Expand your hive! Buy your 2nd Bee Slot (1,000 Honey)."
                7 -> "🎯 Step 7: Tap [HATCH (RNG)] to hatch a new Bee!"
                else -> ""
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E1A17).copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, HoneyAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stepText,
                        color = HoneyGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Skip Tutorial ✕",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onSkipTutorial() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

fun formatNumber(num: Long): String {
    return when {
        num >= 1_000_000_000L -> String.format("%.1fB", num / 1_000_000_000.0)
        num >= 1_000_000L -> String.format("%.1fM", num / 1_000_000.0)
        num >= 1_000L -> String.format("%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
