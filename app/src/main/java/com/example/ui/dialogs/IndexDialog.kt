package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.GameViewModel
import com.example.model.BeeType
import com.example.ui.theme.*

@Composable
fun IndexDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    val totalBeesCount = BeeType.entries.size
    val discoveredCount = state.discoveredBeeIds.size
    val progressFraction = discoveredCount.toFloat() / totalBeesCount

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, HoneyGold, RoundedCornerShape(20.dp)),
            color = GameSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header & Compendium Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📖", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "BEE COMPENDIUM (INDEX)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = HoneyGold
                            )
                            Text(
                                text = "Collection Progress: $discoveredCount / $totalBeesCount (${(progressFraction * 100).toInt()}%)",
                                fontSize = 11.sp,
                                color = HoneyCream
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GameSurfaceElevated)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = HoneyGold,
                    trackColor = GameSurfaceElevated
                )

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 8.dp))

                // 20 Bees Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 130.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BeeType.entries.toList()) { beeType ->
                        val isDiscovered = state.discoveredBeeIds.contains(beeType.id)
                        IndexBeeCard(beeType = beeType, isDiscovered = isDiscovered)
                    }
                }
            }
        }
    }
}

@Composable
private fun IndexBeeCard(
    beeType: BeeType,
    isDiscovered: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDiscovered) GameSurfaceElevated else GameDarkBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDiscovered) beeType.rarity.color else GameBorder
        ),
        modifier = Modifier.height(130.dp)
    ) {
        if (isDiscovered) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = beeType.rarity.displayName,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = beeType.rarity.color
                    )
                    Text(
                        text = "⭐".repeat(beeType.rarity.starCount),
                        fontSize = 7.sp
                    )
                }

                Text(text = "🐝", fontSize = 28.sp)

                Text(
                    text = beeType.beeName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1
                )

                Text(
                    text = beeType.abilityName ?: "Base Gather: ${beeType.baseGatherPower}",
                    fontSize = 8.sp,
                    color = HoneyCream,
                    maxLines = 1
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = beeType.rarity.displayName,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = beeType.rarity.color.copy(alpha = 0.6f)
                )
                Text(text = "❓", fontSize = 28.sp)
                Text(
                    text = "Undiscovered",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
