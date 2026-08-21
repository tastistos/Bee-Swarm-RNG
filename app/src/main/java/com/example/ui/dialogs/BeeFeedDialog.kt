package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.game.GameViewModel
import com.example.model.BeeInstance
import com.example.model.ItemType
import com.example.ui.theme.*

@Composable
fun BeeFeedDialog(
    bee: BeeInstance,
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    val ownedTreats = state.getItemCount(ItemType.TREAT)
    val neededForNext = bee.treatsNeededForNextLevel()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, bee.beeType.rarity.color, RoundedCornerShape(20.dp)),
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bee.beeType.rarity.color.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, bee.beeType.rarity.color)
                        ) {
                            Text(
                                text = bee.beeType.rarity.displayName.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = bee.beeType.rarity.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = bee.beeType.beeName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(34.dp)
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

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 6.dp))

                // Middle: Bee Stats & Bond Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Visual Bee Avatar & Level Status
                    Surface(
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(14.dp),
                        color = GameSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GameBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🐝", fontSize = 52.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Level ${bee.level} / 100",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = HoneyGold
                            )
                            Text(
                                text = if (bee.isEquipped) "Equipped (Slot ${bee.slotIndex + 1})" else "In Inventory",
                                fontSize = 11.sp,
                                color = if (bee.isEquipped) Color(0xFF81C784) else Color.LightGray
                            )
                        }
                    }

                    // Right Column: Stats & Ability Info
                    Column(
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bond Progress Bar
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GameSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Bond Progress",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HoneyCream
                                    )
                                    Text(
                                        text = if (bee.level < 100) "${bee.currentBond} / ${bee.requiredBondToNextLevel} Bond" else "MAX LEVEL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HoneyGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { bee.bondProgressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = HoneyGold,
                                    trackColor = GameDarkBg
                                )
                            }
                        }

                        // Stats Card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GameSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Gather Power:", fontSize = 11.sp, color = Color.LightGray)
                                    Text(
                                        text = "${bee.effectiveGatherPower} Pollen/hit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Flight Speed:", fontSize = 11.sp, color = Color.LightGray)
                                    Text(
                                        text = String.format("%.2fx", bee.effectiveSpeed),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                if (bee.beeType.abilityName != null) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Ability: ${bee.beeType.abilityName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = bee.beeType.rarity.color
                                    )
                                    Text(
                                        text = bee.beeType.abilityDescription ?: "",
                                        fontSize = 10.sp,
                                        color = HoneyCream,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 6.dp))

                // Bottom: Feeding Controls (1 Treat = 10 Bond)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🍬 Owned Treats: $ownedTreats",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoneyGold
                        )
                        Text(
                            text = "1 Treat = +10 Bond",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Feed 1
                        Button(
                            onClick = { viewModel.feedBee(bee, 1) },
                            enabled = ownedTreats >= 1 && bee.level < 100,
                            colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("feed_1_btn")
                        ) {
                            Text(text = "Feed 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        // Feed 10
                        Button(
                            onClick = { viewModel.feedBee(bee, 10) },
                            enabled = ownedTreats >= 1 && bee.level < 100,
                            colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("feed_10_btn")
                        ) {
                            Text(text = "Feed 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        // Feed Next Level
                        Button(
                            onClick = { viewModel.feedBee(bee, neededForNext) },
                            enabled = ownedTreats >= 1 && bee.level < 100,
                            colors = ButtonDefaults.buttonColors(containerColor = HoneyAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).height(40.dp).testTag("feed_next_btn")
                        ) {
                            Text(
                                text = "Next Lv ($neededForNext)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Feed Max
                        Button(
                            onClick = { viewModel.feedBee(bee, ownedTreats) },
                            enabled = ownedTreats >= 1 && bee.level < 100,
                            colors = ButtonDefaults.buttonColors(containerColor = HoneyDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.1f).height(40.dp).testTag("feed_max_btn")
                        ) {
                            Text(text = "Feed Max", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Equip / Unequip Toggle
                    if (bee.isEquipped) {
                        OutlinedButton(
                            onClick = {
                                viewModel.unequipBee(bee)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_unequip_bee")
                        ) {
                            Text(text = "Unequip from Hive", fontSize = 11.sp, color = Color(0xFFFF8A80))
                        }
                    } else {
                        val firstFree = (0 until state.hiveSlots).firstOrNull { slot ->
                            state.equippedBees.none { it.slotIndex == slot }
                        }
                        Button(
                            onClick = {
                                if (firstFree != null) {
                                    viewModel.equipBeeToSlot(bee, firstFree)
                                } else {
                                    viewModel.equipBeeToSlot(bee, 0)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("btn_equip_bee")
                        ) {
                            Text(text = "Equip to Hive Slot", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
