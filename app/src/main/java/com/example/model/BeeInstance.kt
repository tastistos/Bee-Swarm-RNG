package com.example.model

import java.util.UUID

/**
 * An individual bee instance owned by the player.
 * Tracks level, bond, and calculates stats dynamically.
 */
data class BeeInstance(
    val instanceId: String = UUID.randomUUID().toString(),
    val beeType: BeeType,
    var level: Int = 1,
    var currentBond: Int = 0,
    var isEquipped: Boolean = false,
    var slotIndex: Int = -1
) {
    companion object {
        const val MAX_LEVEL = 100
        const val BOND_PER_TREAT = 10

        /**
         * Bond required to advance from [level] to [level + 1].
         * Level 1 -> 2: 10 Bond
         * Level 2 -> 3: 20 Bond
         * Level N -> N + 1: N * 10 Bond
         */
        fun getRequiredBondForLevel(level: Int): Int {
            if (level >= MAX_LEVEL) return 0
            return level * 10
        }
    }

    val requiredBondToNextLevel: Int
        get() = getRequiredBondForLevel(level)

    val bondProgressFraction: Float
        get() {
            if (level >= MAX_LEVEL) return 1.0f
            val req = requiredBondToNextLevel
            return if (req > 0) (currentBond.toFloat() / req).coerceIn(0f, 1f) else 1.0f
        }

    val effectiveGatherPower: Int
        get() {
            val levelMultiplier = 1.0f + (level - 1) * 0.12f
            return (beeType.baseGatherPower * levelMultiplier).toInt().coerceAtLeast(1)
        }

    val effectiveSpeed: Float
        get() {
            val levelSpeedBonus = (level - 1) * 0.012f
            return beeType.baseSpeedMultiplier + levelSpeedBonus
        }

    /**
     * Feeds [treatCount] treats to this bee.
     * 1 Treat = 10 Bond.
     * Handles advancing multiple level thresholds automatically until level 100.
     * Returns the number of levels gained.
     */
    fun feedTreats(treatCount: Int): Int {
        if (treatCount <= 0 || level >= MAX_LEVEL) return 0
        val totalBondToAdd = treatCount * BOND_PER_TREAT
        var remainingBond = currentBond + totalBondToAdd
        var startLevel = level

        while (level < MAX_LEVEL) {
            val needed = getRequiredBondForLevel(level)
            if (remainingBond >= needed) {
                remainingBond -= needed
                level++
            } else {
                break
            }
        }

        if (level >= MAX_LEVEL) {
            currentBond = 0
        } else {
            currentBond = remainingBond
        }

        return level - startLevel
    }

    /**
     * Computes how many treats are needed to reach the next level.
     */
    fun treatsNeededForNextLevel(): Int {
        if (level >= MAX_LEVEL) return 0
        val neededBond = requiredBondToNextLevel - currentBond
        return (neededBond + BOND_PER_TREAT - 1) / BOND_PER_TREAT
    }
}
