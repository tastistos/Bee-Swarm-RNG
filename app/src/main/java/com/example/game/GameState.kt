package com.example.game

import androidx.compose.ui.geometry.Offset
import com.example.model.*
import java.util.UUID

data class FloatingText(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val position: Offset,
    val color: androidx.compose.ui.graphics.Color,
    val isCrit: Boolean = false,
    var ageMs: Long = 0L,
    val maxAgeMs: Long = 1000L
)

data class Particle(
    val id: String = UUID.randomUUID().toString(),
    var position: Offset,
    val velocity: Offset,
    val color: androidx.compose.ui.graphics.Color,
    val size: Float,
    var alpha: Float = 1f,
    var ageMs: Long = 0L,
    val maxAgeMs: Long = 800L
)

data class FlowerState(
    val fieldId: String,
    val position: Offset,
    val flowerColor: FlowerColor,
    var pollenRemaining: Float = 100f,
    val maxPollen: Float = 100f,
    var regenTimerMs: Long = 0L
)

data class SwarmBeeEntity(
    val instance: BeeInstance,
    var position: Offset,
    var velocity: Offset = Offset.Zero,
    var targetFlowerPos: Offset? = null,
    var wingPhase: Float = 0f,
    var isGathering: Boolean = false,
    var gatherTimerMs: Long = 0L
)

data class GameState(
    var honey: Long = 100L,
    var pollen: Long = 0L,
    var hiveSlots: Int = 1,
    var toolId: String = Equipment.PLASTIC_SCOOP.id,
    var backpackId: String = Equipment.POUCH.id,
    var accessoryId: String? = null,
    val ownedEquipmentIds: MutableSet<String> = mutableSetOf(
        Equipment.PLASTIC_SCOOP.id,
        Equipment.POUCH.id
    ),
    val items: MutableMap<String, Int> = mutableMapOf(
        ItemType.TREAT.id to 5,
        ItemType.ROYAL_JELLY.id to 1
    ),
    val ownedBees: MutableList<BeeInstance> = mutableListOf(
        BeeInstance(
            beeType = BeeType.BASIC_BEE,
            level = 1,
            currentBond = 0,
            isEquipped = true,
            slotIndex = 0
        )
    ),
    val discoveredBeeIds: MutableSet<String> = mutableSetOf(BeeType.BASIC_BEE.id),
    val quests: MutableList<Quest> = NPC.getDefaultQuests().toMutableList(),
    var tutorialStep: Int = 1,
    var isTutorialCompleted: Boolean = false,
    var totalPollenCollected: Long = 0L,
    var totalHoneyConverted: Long = 0L,
    var totalRollsCount: Long = 0L
) {
    val currentTool: Equipment
        get() = Equipment.getById(toolId)

    val currentBackpack: Equipment
        get() = Equipment.getById(backpackId)

    val maxPollenCapacity: Long
        get() = currentBackpack.pollenCapacity

    val equippedBees: List<BeeInstance>
        get() = ownedBees.filter { it.isEquipped && it.slotIndex in 0 until hiveSlots }

    val nextSlotCost: Long
        get() {
            // First additional slot costs 1,000, then doubles each time: 1000, 2000, 4000, 8000, 16000...
            val exponent = (hiveSlots - 1).coerceAtLeast(0)
            return if (exponent < 30) 1000L * (1L shl exponent) else Long.MAX_VALUE
        }

    fun canAffordNextSlot(): Boolean = honey >= nextSlotCost

    fun getItemCount(itemType: ItemType): Int = items[itemType.id] ?: 0

    fun addItem(itemType: ItemType, count: Int) {
        val cur = getItemCount(itemType)
        items[itemType.id] = (cur + count).coerceAtLeast(0)
    }

    fun consumeItem(itemType: ItemType, count: Int): Boolean {
        val cur = getItemCount(itemType)
        if (cur >= count) {
            items[itemType.id] = cur - count
            return true
        }
        return false
    }
}
