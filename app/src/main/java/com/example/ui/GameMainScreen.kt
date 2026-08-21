package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.game.ActiveDialog
import com.example.game.GameViewModel
import com.example.model.NPC
import com.example.ui.dialogs.*
import com.example.ui.theme.*

@Composable
fun GameMainScreen(
    viewModel: GameViewModel = viewModel()
) {
    val engine = viewModel.engine
    val gameTick by viewModel.gameTick.collectAsState()
    val activeDialog by viewModel.activeDialog.collectAsState()
    val notificationMessage by viewModel.notificationMessage.collectAsState()
    val isNearHive by engine.isNearHive.collectAsState()
    val currentField by engine.currentField.collectAsState()
    val isConverting by engine.isConvertingHoney.collectAsState()

    // Find nearby NPC if within interaction distance
    val nearbyNpc = remember(engine.playerPos) {
        NPC.ALL_NPCS.firstOrNull { npc ->
            (npc.position - engine.playerPos).getDistance() <= 120f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF161311))
    ) {
        // 1. 2D World Game Canvas
        WorldCanvas(
            engine = engine,
            tick = gameTick,
            onNpcClick = { npc ->
                viewModel.openDialog(ActiveDialog.NpcChat(npc))
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Resource HUD & Tutorial Banner
        TopHud(
            gameState = engine.gameState,
            currentField = currentField,
            onSkipTutorial = { viewModel.skipTutorial() },
            onOpenRngRoll = { viewModel.openDialog(ActiveDialog.RngRoll) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )

        // 3. Left Navigation Menu (Items, Equipment, Bees, Index, Quests)
        LeftNavMenu(
            gameState = engine.gameState,
            onMenuClick = { dialog ->
                viewModel.openDialog(dialog)
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
        )

        // 4. Virtual Joystick (Bottom Left)
        VirtualJoystick(
            onVectorChange = { vector ->
                viewModel.setJoystickVector(vector)
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 76.dp, bottom = 16.dp)
        )

        // 5. Action Controls (Bottom Right)
        ActionControls(
            isNearHive = isNearHive,
            isConverting = isConverting,
            onGatherClick = { viewModel.onGatherClick() },
            onHiveOpenClick = { viewModel.onHiveInteractClick() },
            onConvertClick = { viewModel.onConvertHoneyClick() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 16.dp)
        )

        // 6. Nearby NPC Quick Talk Prompt
        if (nearbyNpc != null) {
            Button(
                onClick = { viewModel.openDialog(ActiveDialog.NpcChat(nearbyNpc)) },
                colors = ButtonDefaults.buttonColors(containerColor = HoneyGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .testTag("btn_talk_npc")
            ) {
                Text(
                    text = "Talk to ${nearbyNpc.name} ${nearbyNpc.emoji}",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }

        // 7. Notification Banner (Popups & Level-ups)
        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 58.dp)
        ) {
            if (notificationMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF2E241E).copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, HoneyGold),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = notificationMessage!!,
                        color = HoneyGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 8. Active Dialog Modals
        when (val dialog = activeDialog) {
            is ActiveDialog.Hive -> HiveDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.BeeDetail -> BeeFeedDialog(bee = dialog.bee, viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.RngRoll -> RngRollDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.Items -> ItemsDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.EquipmentView -> EquipmentDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.BeesList -> BeesListDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.Index -> IndexDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.Quests -> QuestsDialog(viewModel = viewModel, onClose = { viewModel.closeDialog() })
            is ActiveDialog.NpcChat -> NpcDialog(npc = dialog.npc, viewModel = viewModel, onClose = { viewModel.closeDialog() })
            else -> {}
        }
    }
}
