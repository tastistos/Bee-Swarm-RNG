package com.example.ui.dialogs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.ActiveDialog
import com.example.game.GameViewModel
import com.example.model.BeeInstance
import com.example.ui.formatNumber
import com.example.ui.theme.*

@Composable
fun HiveDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    val isConverting by viewModel.engine.isConvertingHoney.collectAsState()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, HoneyGold, RoundedCornerShape(20.dp)),
            color = GameSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🏰", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "HIVE SANCTUARY",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = HoneyGold
                            )
                            Text(
                                text = "${state.equippedBees.size} / ${state.hiveSlots} Slots Filled",
                                fontSize = 12.sp,
                                color = HoneyCream
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GameSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Honeycomb Slots Grid
                val slotsList = (0 until state.hiveSlots).toList()
                val occupiedMap = state.equippedBees.associateBy { it.slotIndex }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 90.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(slotsList) { index, slotIdx ->
                        val bee = occupiedMap[slotIdx]
                        HiveSlotCard(
                            slotIndex = slotIdx,
                            bee = bee,
                            onClick = {
                                if (bee != null) {
                                    viewModel.openDialog(ActiveDialog.BeeDetail(bee))
                                } else {
                                    viewModel.openDialog(ActiveDialog.BeesList)
                                }
                            }
                        )
                    }
                }

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Bottom Action Controls: Slot Purchase & Honey Conversion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Convert Pollen Button
                    Button(
                        onClick = { viewModel.engine.toggleConvertingHoney() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConverting) Color(0xFF43A047) else HoneyAmber
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hive_convert_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🍯", fontSize = 18.sp)
                            Text(
                                text = if (isConverting) "Converting Pollen..." else "Convert All Pollen",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Buy Next Slot Button
                    val canAfford = state.canAffordNextSlot()
                    val nextCost = state.nextSlotCost
                    Button(
                        onClick = { viewModel.buyNextHiveSlot() },
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoneyGold,
                            disabledContainerColor = GameSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("hive_buy_slot_btn")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Buy Slot ${state.hiveSlots + 1}",
                                fontWeight = FontWeight.Black,
                                color = if (canAfford) Color.Black else Color.Gray,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${formatNumber(nextCost)} Honey",
                                fontWeight = FontWeight.Bold,
                                color = if (canAfford) Color(0xFF5D4037) else Color.DarkGray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HiveSlotCard(
    slotIndex: Int,
    bee: BeeInstance?,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (bee != null) GameSurfaceElevated else GameSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            bee?.beeType?.rarity?.color ?: GameBorder
        ),
        modifier = Modifier
            .height(105.dp)
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        if (bee != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Rarity Tag & Level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "S${slotIndex + 1}",
                        fontSize = 9.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lv.${bee.level}",
                        fontSize = 9.sp,
                        color = HoneyGold,
                        fontWeight = FontWeight.Black
                    )
                }

                // Bee Avatar Emoji
                Text(text = "🐝", fontSize = 26.sp)

                // Bee Name
                Text(
                    text = bee.beeType.beeName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = bee.beeType.rarity.color,
                    maxLines = 1
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Slot ${slotIndex + 1}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "➕", fontSize = 20.sp)
                Text(
                    text = "Empty",
                    fontSize = 9.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
