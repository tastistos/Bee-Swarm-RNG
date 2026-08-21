package com.example.ui.dialogs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
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
import com.example.game.RngConfig
import com.example.model.BeeRarity
import com.example.model.ItemType
import com.example.ui.formatNumber
import com.example.ui.theme.*

@Composable
fun RngRollDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    val revealState by viewModel.rollRevealState.collectAsState()
    var selectedEggType by remember { mutableStateOf(RngConfig.EggType.BASIC) }
    var showOddsSheet by remember { mutableStateOf(false) }

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
            color = GameDarkBg
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (revealState.isRevealing && revealState.rolledBee != null) {
                    // Dramatic Reveal Screen
                    RevealScreen(
                        beeType = revealState.rolledBee!!,
                        isNew = revealState.isNew,
                        isDuplicate = revealState.isDuplicate,
                        onRollAgain = {
                            viewModel.dismissRollReveal()
                            viewModel.performRngRoll(selectedEggType)
                        },
                        onDismiss = {
                            viewModel.dismissRollReveal()
                        }
                    )
                } else {
                    // Standard Egg Selection & Roll Menu
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
                                Text(text = "🥚", fontSize = 24.sp)
                                Text(
                                    text = "BEE SWARM RNG HATCHER",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = HoneyGold
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { showOddsSheet = !showOddsSheet },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(GameSurfaceElevated)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Odds",
                                        tint = HoneyGold
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
                        }

                        Divider(color = GameBorder, modifier = Modifier.padding(vertical = 4.dp))

                        // Middle: Egg Type Cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 1. Basic Egg Card
                            EggSelectionCard(
                                title = "Basic Egg",
                                emoji = "🥚",
                                costText = "${RngConfig.BASIC_EGG_HONEY_COST} Honey",
                                subtitle = "Common to Secret",
                                isSelected = selectedEggType == RngConfig.EggType.BASIC,
                                accentColor = HoneyGold,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedEggType = RngConfig.EggType.BASIC }
                            )

                            // 2. Royal Jelly Card
                            val jellyCount = state.getItemCount(ItemType.ROYAL_JELLY)
                            EggSelectionCard(
                                title = "Royal Jelly",
                                emoji = "🍯",
                                costText = if (jellyCount > 0) "1 Jelly ($jellyCount left)" else "${formatNumber(RngConfig.ROYAL_JELLY_HONEY_COST)} Honey",
                                subtitle = "Guaranteed Rare+",
                                isSelected = selectedEggType == RngConfig.EggType.ROYAL_JELLY,
                                accentColor = Color(0xFFAB47BC),
                                modifier = Modifier.weight(1f),
                                onClick = { selectedEggType = RngConfig.EggType.ROYAL_JELLY }
                            )

                            // 3. Golden Egg Card
                            val eggCount = state.getItemCount(ItemType.GOLDEN_EGG)
                            EggSelectionCard(
                                title = "Golden Egg",
                                emoji = "🌟",
                                costText = if (eggCount > 0) "1 Egg ($eggCount left)" else "${formatNumber(RngConfig.GOLDEN_EGG_HONEY_COST)} Honey",
                                subtitle = "Guaranteed Epic+",
                                isSelected = selectedEggType == RngConfig.EggType.GOLDEN,
                                accentColor = Color(0xFFFFD700),
                                modifier = Modifier.weight(1f),
                                onClick = { selectedEggType = RngConfig.EggType.GOLDEN }
                            )
                        }

                        Divider(color = GameBorder, modifier = Modifier.padding(vertical = 4.dp))

                        // Bottom Roll Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.performRngRoll(selectedEggType)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_hatch_1x")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "✨", fontSize = 18.sp)
                                    Text(
                                        text = "HATCH 1x (${selectedEggType.displayName})",
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Odds Breakdown Sheet Overlay
                    if (showOddsSheet) {
                        OddsSheet(
                            eggType = selectedEggType,
                            onClose = { showOddsSheet = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EggSelectionCard(
    title: String,
    emoji: String,
    costText: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) GameSurfaceElevated else GameSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) accentColor else GameBorder
        ),
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )

            Text(text = emoji, fontSize = 42.sp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Text(
                    text = costText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = HoneyGold
                )
            }
        }
    }
}

@Composable
private fun RevealScreen(
    beeType: com.example.model.BeeType,
    isNew: Boolean,
    isDuplicate: Boolean,
    onRollAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C0A))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isNew) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFD54F),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⭐ NEW DISCOVERY! ⭐",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                if (isDuplicate) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF64B5F6),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DUPLICATE (Added to Bees)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Big Bee Avatar with Rarity Aura
            Surface(
                shape = CircleShape,
                color = GameSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(3.dp, beeType.rarity.color),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🐝", fontSize = 54.sp)
                }
            }

            // Name & Rarity
            Text(
                text = beeType.beeName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = beeType.rarity.color
            )

            Text(
                text = "${beeType.rarity.displayName} • Base Gather: ${beeType.baseGatherPower} Pollen",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (beeType.abilityName != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GameSurfaceElevated,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ability: ${beeType.abilityName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = beeType.rarity.color
                        )
                        Text(
                            text = beeType.abilityDescription ?: "",
                            fontSize = 10.sp,
                            color = HoneyCream,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text(text = "Collect & Return", color = Color.White, fontSize = 11.sp)
                }

                Button(
                    onClick = onRollAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text(text = "Hatch Again", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun OddsSheet(
    eggType: RngConfig.EggType,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(16.dp),
        color = GameSurfaceDark,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, HoneyGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 ${eggType.displayName} Odds Breakdown",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = HoneyGold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Divider(color = GameBorder, modifier = Modifier.padding(vertical = 4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(BeeRarity.entries.toList()) { rarity ->
                    val odds = eggType.oddsTable[rarity] ?: 0.0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(GameSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rarity.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = rarity.color
                        )
                        Text(
                            text = if (odds > 0) String.format("%.3f%%", odds) else "0.0%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (odds > 0) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    }
}
