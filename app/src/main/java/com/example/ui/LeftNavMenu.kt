package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ActiveDialog
import com.example.game.GameState
import com.example.ui.theme.*

@Composable
fun LeftNavMenu(
    gameState: GameState,
    onMenuClick: (ActiveDialog) -> Unit,
    modifier: Modifier = Modifier
) {
    val unclaimedQuestsCount = gameState.quests.count { it.isCompleted && !it.isClaimed }
    val totalTreats = gameState.getItemCount(com.example.model.ItemType.TREAT)

    Column(
        modifier = modifier
            .width(62.dp)
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(GameSurfaceDark.copy(alpha = 0.90f))
            .border(1.dp, GameBorder, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Items
        NavMenuItem(
            emoji = "🎒",
            label = "Items",
            badgeText = if (totalTreats > 0) "$totalTreats" else null,
            testTag = "nav_btn_items",
            onClick = { onMenuClick(ActiveDialog.Items) }
        )

        // 2. Equipment
        NavMenuItem(
            emoji = "🛠️",
            label = "Equip",
            testTag = "nav_btn_equip",
            onClick = { onMenuClick(ActiveDialog.EquipmentView) }
        )

        // 3. Bees
        NavMenuItem(
            emoji = "🐝",
            label = "Bees",
            badgeText = "${gameState.ownedBees.size}",
            testTag = "nav_btn_bees",
            onClick = { onMenuClick(ActiveDialog.BeesList) }
        )

        // 4. Index
        val discoveredRatio = "${gameState.discoveredBeeIds.size}/20"
        NavMenuItem(
            emoji = "📖",
            label = "Index",
            badgeText = discoveredRatio,
            testTag = "nav_btn_index",
            onClick = { onMenuClick(ActiveDialog.Index) }
        )

        // 5. Quests
        NavMenuItem(
            emoji = "📜",
            label = "Quests",
            badgeText = if (unclaimedQuestsCount > 0) "!" else null,
            badgeColor = Color(0xFFFF1744),
            testTag = "nav_btn_quests",
            onClick = { onMenuClick(ActiveDialog.Quests) }
        )
    }
}

@Composable
private fun NavMenuItem(
    emoji: String,
    label: String,
    badgeText: String? = null,
    badgeColor: Color = HoneyGold,
    testTag: String,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (badgeText != null) {
                Badge(
                    containerColor = badgeColor,
                    contentColor = Color.Black,
                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                ) {
                    Text(text = badgeText, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .size(width = 54.dp, height = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GameSurfaceElevated.copy(alpha = 0.8f))
                .border(1.dp, HoneyAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable { onClick() }
                .testTag(testTag)
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = HoneyCream
            )
        }
    }
}
