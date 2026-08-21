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
import com.example.model.NPC
import com.example.model.Quest
import com.example.ui.formatNumber
import com.example.ui.theme.*

@Composable
fun QuestsDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val state = viewModel.engine.gameState
    var selectedNpcId by remember { mutableStateOf(NPC.ALL_NPCS.first().id) }

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
                        Text(text = "📜", fontSize = 22.sp)
                        Text(
                            text = "QUEST JOURNAL",
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

                // NPC Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NPC.ALL_NPCS.forEach { npc ->
                        val isSelected = npc.id == selectedNpcId
                        Button(
                            onClick = { selectedNpcId = npc.id },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) HoneyGold else GameSurfaceElevated
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.weight(1f).height(32.dp)
                        ) {
                            Text(
                                text = "${npc.emoji} ${npc.name.split(" ")[0]}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.LightGray
                            )
                        }
                    }
                }

                Divider(color = GameBorder, modifier = Modifier.padding(vertical = 6.dp))

                val filteredQuests = state.quests.filter { it.npcId == selectedNpcId }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredQuests) { quest ->
                        QuestCard(
                            quest = quest,
                            onClaim = {
                                viewModel.claimQuest(quest)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestCard(
    quest: Quest,
    onClaim: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (quest.isClaimed) GameDarkBg else GameSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isCompleted && !quest.isClaimed) Color(0xFF81C784) else GameBorder
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (quest.isClaimed) Color.Gray else Color.White
                )
                Text(
                    text = quest.description,
                    fontSize = 10.sp,
                    color = if (quest.isClaimed) Color.DarkGray else HoneyCream
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { quest.progressFraction },
                        modifier = Modifier
                            .width(130.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) Color(0xFF43A047) else HoneyGold,
                        trackColor = GameDarkBg
                    )
                    Text(
                        text = "${formatNumber(quest.currentProgress)} / ${formatNumber(quest.targetAmount)}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quest.isCompleted) Color(0xFF81C784) else Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                // Rewards Summary
                val rewards = mutableListOf<String>()
                if (quest.rewardHoney > 0) rewards.add("+${formatNumber(quest.rewardHoney)} Honey")
                if (quest.rewardTreats > 0) rewards.add("+${quest.rewardTreats} Treats")
                if (quest.rewardRoyalJelly > 0) rewards.add("+${quest.rewardRoyalJelly} Jelly")
                if (quest.rewardGoldenEggs > 0) rewards.add("+${quest.rewardGoldenEggs} Egg")

                Text(
                    text = "Rewards: ${rewards.joinToString(" • ")}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = HoneyGold
                )
            }

            // Claim / Status
            if (quest.isClaimed) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.DarkGray
                ) {
                    Text(
                        text = "CLAIMED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else if (quest.isCompleted) {
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("claim_quest_${quest.id}")
                ) {
                    Text(text = "CLAIM", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GameSurfaceDark
                ) {
                    Text(
                        text = "In Progress",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
