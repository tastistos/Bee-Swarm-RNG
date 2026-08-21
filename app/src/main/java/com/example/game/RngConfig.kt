package com.example.game

import com.example.model.BeeRarity
import com.example.model.BeeType
import kotlin.random.Random

data class RngRollResult(
    val beeType: BeeType,
    val isNewDiscovery: Boolean,
    val isDuplicate: Boolean,
    val eggTypeName: String
)

object RngConfig {
    const val BASIC_EGG_HONEY_COST = 250L
    const val ROYAL_JELLY_HONEY_COST = 5000L
    const val GOLDEN_EGG_HONEY_COST = 50000L

    /**
     * Standard odds table for Basic Egg. Sums to 100%.
     */
    val BASIC_EGG_ODDS: Map<BeeRarity, Double> = mapOf(
        BeeRarity.COMMON to 50.0,
        BeeRarity.UNCOMMON to 30.0,
        BeeRarity.RARE to 14.0,
        BeeRarity.EPIC to 4.5,
        BeeRarity.LEGENDARY to 1.2,
        BeeRarity.MYTHIC to 0.25,
        BeeRarity.DIVINE to 0.04,
        BeeRarity.CELESTIAL to 0.009,
        BeeRarity.SECRET to 0.001
    )

    /**
     * Boosted odds table for Royal Jelly (Guaranteed Rare+).
     */
    val ROYAL_JELLY_ODDS: Map<BeeRarity, Double> = mapOf(
        BeeRarity.RARE to 70.0,
        BeeRarity.EPIC to 22.0,
        BeeRarity.LEGENDARY to 6.5,
        BeeRarity.MYTHIC to 1.2,
        BeeRarity.DIVINE to 0.25,
        BeeRarity.CELESTIAL to 0.045,
        BeeRarity.SECRET to 0.005
    )

    /**
     * High tier odds table for Golden Egg (Guaranteed Epic+).
     */
    val GOLDEN_EGG_ODDS: Map<BeeRarity, Double> = mapOf(
        BeeRarity.EPIC to 65.0,
        BeeRarity.LEGENDARY to 25.0,
        BeeRarity.MYTHIC to 8.0,
        BeeRarity.DIVINE to 1.6,
        BeeRarity.CELESTIAL to 0.35,
        BeeRarity.SECRET to 0.05
    )

    enum class EggType(val displayName: String, val costHoney: Long, val oddsTable: Map<BeeRarity, Double>) {
        BASIC("Basic Egg", BASIC_EGG_HONEY_COST, BASIC_EGG_ODDS),
        ROYAL_JELLY("Royal Jelly", ROYAL_JELLY_HONEY_COST, ROYAL_JELLY_ODDS),
        GOLDEN("Golden Egg", GOLDEN_EGG_HONEY_COST, GOLDEN_EGG_ODDS)
    }

    /**
     * Performs a single weighted RNG roll according to the selected EggType.
     */
    fun rollBee(eggType: EggType): BeeType {
        val odds = eggType.oddsTable
        val totalWeight = odds.values.sum()
        val randomPoint = Random.nextDouble(0.0, totalWeight)

        var cumulative = 0.0
        var selectedRarity = odds.keys.first()

        for ((rarity, weight) in odds) {
            cumulative += weight
            if (randomPoint <= cumulative) {
                selectedRarity = rarity
                break
            }
        }

        val candidates = BeeType.entries.filter { it.rarity == selectedRarity }
        return if (candidates.isNotEmpty()) {
            candidates.random()
        } else {
            BeeType.BASIC_BEE
        }
    }
}
