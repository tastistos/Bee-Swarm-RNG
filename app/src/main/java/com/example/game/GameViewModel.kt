package com.example.game

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GameAudio
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed class ActiveDialog {
    object None : ActiveDialog()
    object Hive : ActiveDialog()
    data class BeeDetail(val bee: BeeInstance) : ActiveDialog()
    object RngRoll : ActiveDialog()
    object Items : ActiveDialog()
    object EquipmentView : ActiveDialog()
    object BeesList : ActiveDialog()
    object Index : ActiveDialog()
    object Quests : ActiveDialog()
    data class NpcChat(val npc: NPC) : ActiveDialog()
    object Settings : ActiveDialog()
    object OddsInfo : ActiveDialog()
}

data class RollRevealState(
    val isRevealing: Boolean = false,
    val rolledBee: BeeType? = null,
    val isNew: Boolean = false,
    val isDuplicate: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val saveManager = GameSaveManager(application)
    val engine = GameEngine(saveManager)

    private val _activeDialog = MutableStateFlow<ActiveDialog>(ActiveDialog.None)
    val activeDialog = _activeDialog.asStateFlow()

    private val _rollRevealState = MutableStateFlow(RollRevealState())
    val rollRevealState = _rollRevealState.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage = _notificationMessage.asStateFlow()

    private val _joystickVector = MutableStateFlow(Offset.Zero)
    val joystickVector = _joystickVector.asStateFlow()

    // Fast UI tick for smooth 60fps rendering in Compose
    private val _gameTick = MutableStateFlow(0L)
    val gameTick = _gameTick.asStateFlow()

    init {
        GameAudio.startBgm()
        startGameLoop()
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                val now = System.nanoTime()
                val deltaSec = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastTime = now

                engine.update(deltaSec, _joystickVector.value)
                _gameTick.value = engine.worldTick
                delay(16) // ~60 FPS
            }
        }
    }

    fun setJoystickVector(vector: Offset) {
        _joystickVector.value = vector
    }

    fun openDialog(dialog: ActiveDialog) {
        GameAudio.playButtonClick()
        _activeDialog.value = dialog
    }

    fun closeDialog() {
        GameAudio.playButtonClick()
        _activeDialog.value = ActiveDialog.None
    }

    fun onGatherClick() {
        engine.gatherPlayerPollen()
    }

    fun onHiveInteractClick() {
        GameAudio.playButtonClick()
        _activeDialog.value = ActiveDialog.Hive
        if (engine.gameState.tutorialStep == 4) {
            engine.gameState.tutorialStep = 5
        }
    }

    fun onConvertHoneyClick() {
        engine.toggleConvertingHoney()
    }

    fun buyNextHiveSlot() {
        val success = engine.buyNextHiveSlot()
        if (success) {
            showNotification("Unlocked Bee Slot ${engine.gameState.hiveSlots}!")
        } else {
            showNotification("Not enough Honey for next slot!")
        }
    }

    fun feedBee(bee: BeeInstance, treatCount: Int) {
        val currentTreats = engine.gameState.getItemCount(ItemType.TREAT)
        val actualFeed = treatCount.coerceAtMost(currentTreats)
        if (actualFeed <= 0) {
            showNotification("No Treats in inventory!")
            return
        }

        engine.gameState.consumeItem(ItemType.TREAT, actualFeed)
        val levelsGained = bee.feedTreats(actualFeed)
        engine.trackQuestProgress(QuestObjectiveType.FEED_TREATS, actualFeed.toLong())

        if (levelsGained > 0) {
            GameAudio.playLevelUp()
            showNotification("${bee.beeType.beeName} grew to Level ${bee.level}! (+${levelsGained} Lv)")
            engine.trackQuestProgress(QuestObjectiveType.LEVEL_UP_BEE, bee.level.toLong())
        } else {
            GameAudio.playButtonClick()
            showNotification("Fed $actualFeed Treats! (+${actualFeed * 10} Bond)")
        }

        // Tutorial Step 5 Check
        if (engine.gameState.tutorialStep == 5) {
            engine.gameState.tutorialStep = 6
        }

        saveManager.saveGame(engine.gameState)
    }

    fun equipBeeToSlot(bee: BeeInstance, slotIndex: Int) {
        if (slotIndex < 0 || slotIndex >= engine.gameState.hiveSlots) return

        // If another bee is in this slot, unequip it
        for (b in engine.gameState.ownedBees) {
            if (b.slotIndex == slotIndex) {
                b.isEquipped = false
                b.slotIndex = -1
            }
        }

        bee.isEquipped = true
        bee.slotIndex = slotIndex
        engine.syncSwarmEntities()
        GameAudio.playButtonClick()
        showNotification("Equipped ${bee.beeType.beeName} to Slot ${slotIndex + 1}!")
        saveManager.saveGame(engine.gameState)
    }

    fun unequipBee(bee: BeeInstance) {
        bee.isEquipped = false
        bee.slotIndex = -1
        engine.syncSwarmEntities()
        GameAudio.playButtonClick()
        showNotification("Unequipped ${bee.beeType.beeName}.")
        saveManager.saveGame(engine.gameState)
    }

    fun performRngRoll(eggType: RngConfig.EggType) {
        var canRoll = false
        when (eggType) {
            RngConfig.EggType.BASIC -> {
                if (engine.gameState.honey >= RngConfig.BASIC_EGG_HONEY_COST) {
                    engine.gameState.honey -= RngConfig.BASIC_EGG_HONEY_COST
                    canRoll = true
                } else {
                    showNotification("Need ${RngConfig.BASIC_EGG_HONEY_COST} Honey for Basic Egg!")
                }
            }
            RngConfig.EggType.ROYAL_JELLY -> {
                if (engine.gameState.consumeItem(ItemType.ROYAL_JELLY, 1)) {
                    canRoll = true
                } else if (engine.gameState.honey >= RngConfig.ROYAL_JELLY_HONEY_COST) {
                    engine.gameState.honey -= RngConfig.ROYAL_JELLY_HONEY_COST
                    canRoll = true
                } else {
                    showNotification("Need 1 Royal Jelly or ${RngConfig.ROYAL_JELLY_HONEY_COST} Honey!")
                }
            }
            RngConfig.EggType.GOLDEN -> {
                if (engine.gameState.consumeItem(ItemType.GOLDEN_EGG, 1)) {
                    canRoll = true
                } else if (engine.gameState.honey >= RngConfig.GOLDEN_EGG_HONEY_COST) {
                    engine.gameState.honey -= RngConfig.GOLDEN_EGG_HONEY_COST
                    canRoll = true
                } else {
                    showNotification("Need 1 Golden Egg or ${RngConfig.GOLDEN_EGG_HONEY_COST} Honey!")
                }
            }
        }

        if (!canRoll) return

        GameAudio.playRngRoll()
        val rolledBee = RngConfig.rollBee(eggType)
        val isNew = !engine.gameState.discoveredBeeIds.contains(rolledBee.id)
        val isDuplicate = engine.gameState.ownedBees.any { it.beeType == rolledBee }

        engine.gameState.discoveredBeeIds.add(rolledBee.id)
        engine.gameState.totalRollsCount++
        engine.trackQuestProgress(QuestObjectiveType.ROLL_BEE, 1L)

        // Create new bee instance
        val newBee = BeeInstance(beeType = rolledBee, level = 1, currentBond = 0)
        // Auto equip to empty slot if available
        val occupiedSlots = engine.gameState.equippedBees.map { it.slotIndex }.toSet()
        val firstFreeSlot = (0 until engine.gameState.hiveSlots).firstOrNull { it !in occupiedSlots }
        if (firstFreeSlot != null) {
            newBee.isEquipped = true
            newBee.slotIndex = firstFreeSlot
        }
        engine.gameState.ownedBees.add(newBee)
        engine.syncSwarmEntities()
        engine.trackQuestProgress(QuestObjectiveType.OWN_BEES_COUNT, engine.gameState.ownedBees.size.toLong())

        _rollRevealState.value = RollRevealState(
            isRevealing = true,
            rolledBee = rolledBee,
            isNew = isNew,
            isDuplicate = isDuplicate
        )

        GameAudio.playRngReveal(rolledBee.rarity.isHighTier())

        // Tutorial Step 7 Check
        if (engine.gameState.tutorialStep == 7) {
            engine.gameState.tutorialStep = 8
            engine.gameState.isTutorialCompleted = true
        }

        saveManager.saveGame(engine.gameState)
    }

    fun dismissRollReveal() {
        _rollRevealState.value = RollRevealState(isRevealing = false)
    }

    fun buyEquipment(equipment: Equipment) {
        if (engine.gameState.ownedEquipmentIds.contains(equipment.id)) {
            // Already owned -> Equip
            when (equipment.type) {
                EquipmentType.TOOL -> engine.gameState.toolId = equipment.id
                EquipmentType.BACKPACK -> engine.gameState.backpackId = equipment.id
                EquipmentType.ACCESSORY -> engine.gameState.accessoryId = equipment.id
            }
            GameAudio.playButtonClick()
            showNotification("Equipped ${equipment.name}!")
            saveManager.saveGame(engine.gameState)
            return
        }

        if (engine.gameState.honey < equipment.costHoney) {
            showNotification("Not enough Honey to purchase ${equipment.name}!")
            return
        }

        engine.gameState.honey -= equipment.costHoney
        engine.gameState.ownedEquipmentIds.add(equipment.id)
        when (equipment.type) {
            EquipmentType.TOOL -> engine.gameState.toolId = equipment.id
            EquipmentType.BACKPACK -> engine.gameState.backpackId = equipment.id
            EquipmentType.ACCESSORY -> engine.gameState.accessoryId = equipment.id
        }
        GameAudio.playSlotPurchase()
        showNotification("Purchased & Equipped ${equipment.name}!")
        saveManager.saveGame(engine.gameState)
    }

    fun useItem(itemType: ItemType) {
        when (itemType) {
            ItemType.ROYAL_JELLY -> {
                openDialog(ActiveDialog.RngRoll)
            }
            ItemType.GOLDEN_EGG -> {
                openDialog(ActiveDialog.RngRoll)
            }
            ItemType.HONEY_POTION -> {
                if (engine.gameState.consumeItem(ItemType.HONEY_POTION, 1)) {
                    val p = engine.gameState.pollen
                    engine.gameState.pollen = 0
                    engine.gameState.honey += p
                    engine.gameState.totalHoneyConverted += p
                    GameAudio.playConvertHoney()
                    showNotification("Used Honey Potion! Converted $p Pollen into Honey!")
                    saveManager.saveGame(engine.gameState)
                }
            }
            ItemType.SUPER_SMOOTHIE -> {
                if (engine.gameState.consumeItem(ItemType.SUPER_SMOOTHIE, 1)) {
                    GameAudio.playLevelUp()
                    showNotification("Slurped Super Smoothie! Super Speed & 2x Pollen Active!")
                }
            }
            ItemType.TREAT -> {
                openDialog(ActiveDialog.BeesList)
            }
        }
    }

    fun claimQuest(quest: Quest) {
        if (!quest.isCompleted || quest.isClaimed) return
        quest.isClaimed = true

        engine.gameState.honey += quest.rewardHoney
        if (quest.rewardTreats > 0) engine.gameState.addItem(ItemType.TREAT, quest.rewardTreats)
        if (quest.rewardRoyalJelly > 0) engine.gameState.addItem(ItemType.ROYAL_JELLY, quest.rewardRoyalJelly)
        if (quest.rewardGoldenEggs > 0) engine.gameState.addItem(ItemType.GOLDEN_EGG, quest.rewardGoldenEggs)

        GameAudio.playQuestComplete()
        showNotification("Claimed quest: ${quest.title}! (+${quest.rewardHoney} Honey)")
        saveManager.saveGame(engine.gameState)
    }

    fun skipTutorial() {
        engine.gameState.isTutorialCompleted = true
        engine.gameState.tutorialStep = 8
        showNotification("Tutorial skipped. Have fun beekeeping!")
        saveManager.saveGame(engine.gameState)
    }

    fun showNotification(msg: String) {
        _notificationMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_notificationMessage.value == msg) {
                _notificationMessage.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        GameAudio.stopBgm()
        saveManager.saveGame(engine.gameState)
    }
}
