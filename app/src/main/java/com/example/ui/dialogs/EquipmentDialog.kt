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
import com.example.model.Equipment
import com.example.model.EquipmentType
import com.example.ui.formatNumber
import com.example.ui.theme.*

@Composable
fun EquipmentDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    var selectedTab by remember { mutableStateOf(EquipmentType.TOOL) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
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
                        Text(text = "🛠️", fontSize = 22.sp)
                        Text(
                            text = "EQUIPMENT & GEAR",
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

                Spacer(modifier = Modifier.height(6.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EquipmentTabButton(
                        label = "Tools (Gather)",
                        isSelected = selectedTab == EquipmentType.TOOL,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = EquipmentType.TOOL }
                    )
                    EquipmentTabButton(
                        label = "Backpacks (Capacity)",
                        isSelected = selectedTab == EquipmentType.BACKPACK,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = EquipmentType.BACKPACK }
                    )
                    EquipmentTabButton(
                        label = "Accessories",
                        isSelected = selectedTab == EquipmentType.ACCESSORY,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = EquipmentType.ACCESSORY }
                    )
                }

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 8.dp))

                val filteredEquipment = Equipment.ALL_EQUIPMENT.filter { it.type == selectedTab }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredEquipment) { equip ->
                        val isEquipped = when (equip.type) {
                            EquipmentType.TOOL -> state.toolId == equip.id
                            EquipmentType.BACKPACK -> state.backpackId == equip.id
                            EquipmentType.ACCESSORY -> state.accessoryId == equip.id
                        }
                        val isOwned = state.ownedEquipmentIds.contains(equip.id)
                        val canAfford = state.honey >= equip.costHoney

                        EquipmentItemCard(
                            equipment = equip,
                            isEquipped = isEquipped,
                            isOwned = isOwned,
                            canAfford = canAfford,
                            onAction = {
                                viewModel.buyEquipment(equip)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentTabButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) HoneyGold else GameSurfaceElevated
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = modifier.height(34.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.Black else Color.LightGray
        )
    }
}

@Composable
private fun EquipmentItemCard(
    equipment: Equipment,
    isEquipped: Boolean,
    isOwned: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isEquipped) GameSurfaceElevated else GameSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            if (isEquipped) 2.dp else 1.dp,
            if (isEquipped) HoneyGold else GameBorder
        ),
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
                Text(text = equipment.iconEmoji, fontSize = 28.sp)
                Column {
                    Text(
                        text = equipment.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = equipment.description,
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                    // Stats line
                    val statsText = when (equipment.type) {
                        EquipmentType.TOOL -> "+${equipment.pollenPerGather} Pollen/Gather • ${equipment.gatherCooldownMs}ms speed"
                        EquipmentType.BACKPACK -> "Capacity: ${formatNumber(equipment.pollenCapacity)} Pollen"
                        EquipmentType.ACCESSORY -> "Speed: +${(equipment.moveSpeedBonus * 100).toInt()}% • Convert: +${(equipment.convertSpeedBonus * 100).toInt()}%"
                    }
                    Text(
                        text = statsText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoneyGold
                    )
                }
            }

            // Action Button (Equipped / Equip / Buy)
            if (isEquipped) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Text(
                        text = "EQUIPPED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else if (isOwned) {
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("equip_${equipment.id}")
                ) {
                    Text(text = "Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            } else {
                Button(
                    onClick = onAction,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoneyAmber,
                        disabledContainerColor = GameSurfaceElevated
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("buy_${equipment.id}")
                ) {
                    Text(
                        text = "${formatNumber(equipment.costHoney)} Honey",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (canAfford) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}
