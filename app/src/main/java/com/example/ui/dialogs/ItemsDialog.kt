package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.GameViewModel
import com.example.model.ItemType
import com.example.ui.theme.*

@Composable
fun ItemsDialog(
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
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.88f)
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
                        Text(text = "🎒", fontSize = 22.sp)
                        Text(
                            text = "ITEM INVENTORY",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = HoneyGold
                        )
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ItemType.entries.toList()) { itemType ->
                        val count = state.getItemCount(itemType)
                        ItemRowCard(
                            itemType = itemType,
                            count = count,
                            onUse = {
                                viewModel.useItem(itemType)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRowCard(
    itemType: ItemType,
    count: Int,
    onUse: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GameSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, GameBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = itemType.emoji, fontSize = 28.sp)
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = itemType.itemName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = HoneyAmber.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "x$count",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HoneyGold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = itemType.description,
                        fontSize = 10.sp,
                        color = HoneyCream,
                        lineHeight = 12.sp
                    )
                }
            }

            if (itemType.isUsable) {
                Button(
                    onClick = onUse,
                    enabled = count > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("use_item_${itemType.id}")
                ) {
                    Text(text = "Use", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
