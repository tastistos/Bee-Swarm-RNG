package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.game.ActiveDialog
import com.example.game.GameViewModel
import com.example.model.BeeInstance
import com.example.ui.theme.*

@Composable
fun BeesListDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState

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
                    .padding(16.dp)
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
                        Text(text = "🐝", fontSize = 24.sp)
                        Column {
                            Text(
                                text = "BEE SWARM INVENTORY",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = HoneyGold
                            )
                            Text(
                                text = "${state.ownedBees.size} Total Bees Owned (${state.equippedBees.size}/${state.hiveSlots} Equipped)",
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

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.ownedBees) { bee ->
                        OwnedBeeCard(
                            bee = bee,
                            onClick = {
                                viewModel.openDialog(ActiveDialog.BeeDetail(bee))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnedBeeCard(
    bee: BeeInstance,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GameSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            if (bee.isEquipped) 2.dp else 1.dp,
            if (bee.isEquipped) HoneyGold else bee.beeType.rarity.color
        ),
        modifier = Modifier
            .height(115.dp)
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bee.beeType.rarity.displayName,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = bee.beeType.rarity.color
                )
                Text(
                    text = "Lv.${bee.level}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = HoneyGold
                )
            }

            Text(text = "🐝", fontSize = 28.sp)

            Text(
                text = bee.beeType.beeName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1
            )

            if (bee.isEquipped) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Text(
                        text = "Slot ${bee.slotIndex + 1}",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            } else {
                Text(
                    text = "Tap to Manage",
                    fontSize = 8.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
