package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

/**
 * Ordered list of Bee Rarities from lowest to highest.
 */
enum class BeeRarity(
    val displayName: String,
    val rank: Int,
    val color: Color,
    val oddsPercent: Double,
    val glowColor: Color,
    val starCount: Int
) {
    COMMON(
        displayName = "Common",
        rank = 1,
        color = RarityCommon,
        oddsPercent = 50.0,
        glowColor = Color(0xFFCFD8DC),
        starCount = 1
    ),
    UNCOMMON(
        displayName = "Uncommon",
        rank = 2,
        color = RarityUncommon,
        oddsPercent = 30.0,
        glowColor = Color(0xFFA5D6A7),
        starCount = 2
    ),
    RARE(
        displayName = "Rare",
        rank = 3,
        color = RarityRare,
        oddsPercent = 14.0,
        glowColor = Color(0xFF90CAF9),
        starCount = 3
    ),
    EPIC(
        displayName = "Epic",
        rank = 4,
        color = RarityEpic,
        oddsPercent = 4.5,
        glowColor = Color(0xFFCE93D8),
        starCount = 4
    ),
    LEGENDARY(
        displayName = "Legendary",
        rank = 5,
        color = RarityLegendary,
        oddsPercent = 1.2,
        glowColor = Color(0xFFFFCC80),
        starCount = 5
    ),
    MYTHIC(
        displayName = "Mythic",
        rank = 6,
        color = RarityMythic,
        oddsPercent = 0.25,
        glowColor = Color(0xFFF48FB1),
        starCount = 6
    ),
    DIVINE(
        displayName = "Divine",
        rank = 7,
        color = RarityDivine,
        oddsPercent = 0.04,
        glowColor = Color(0xFFFFEE58),
        starCount = 7
    ),
    CELESTIAL(
        displayName = "Celestial",
        rank = 8,
        color = RarityCelestial,
        oddsPercent = 0.009,
        glowColor = Color(0xFF80DEEA),
        starCount = 8
    ),
    SECRET(
        displayName = "Secret",
        rank = 9,
        color = RaritySecret,
        oddsPercent = 0.001,
        glowColor = Color(0xFFFF5252),
        starCount = 9
    );

    fun isHighTier(): Boolean = rank >= 5
}
