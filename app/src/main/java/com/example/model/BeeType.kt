package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * All 20 bee types distributed precisely across the 9 rarities.
 */
enum class BeeType(
    val id: String,
    val beeName: String,
    val rarity: BeeRarity,
    val description: String,
    val baseGatherPower: Int,
    val baseSpeedMultiplier: Float,
    val abilityName: String?,
    val abilityDescription: String?,
    val abilityType: AbilityType = AbilityType.NONE,
    val abilityValue: Float = 0f,
    val primaryColor: Color,
    val stripeColor: Color,
    val wingColor: Color,
    val eyeColor: Color = Color.Black
) {
    // 1. Common (1 Type) - Guaranteed starter bee, weak gather rate, no special ability
    BASIC_BEE(
        id = "basic_bee",
        beeName = "Basic Bee",
        rarity = BeeRarity.COMMON,
        description = "A dependable, cheerful starter bee with classic honey stripes. Always ready to gather pollen.",
        baseGatherPower = 2,
        baseSpeedMultiplier = 1.0f,
        abilityName = null,
        abilityDescription = "No special ability. Honest hard worker.",
        abilityType = AbilityType.NONE,
        abilityValue = 0f,
        primaryColor = Color(0xFFFFD54F),
        stripeColor = Color(0xFF263238),
        wingColor = Color(0xFFE0F7FA)
    ),

    // 2. Uncommon (4 Types)
    BASTY_BEE(
        id = "basty_bee",
        beeName = "Basty Bee",
        rarity = BeeRarity.UNCOMMON,
        description = "Quick on its wings and loves dashing between flower blossoms.",
        baseGatherPower = 4,
        baseSpeedMultiplier = 1.3f,
        abilityName = null,
        abilityDescription = "Inherent high flight speed (+30% Bee Speed).",
        abilityType = AbilityType.NONE,
        abilityValue = 0f,
        primaryColor = Color(0xFFFFF176),
        stripeColor = Color(0xFF4E342E),
        wingColor = Color(0xFFE1F5FE)
    ),
    COOL_BEE(
        id = "cool_bee",
        beeName = "Cool Bee",
        rarity = BeeRarity.UNCOMMON,
        description = "Wears chilled blue shades. Loves cooling blue flower nectar.",
        baseGatherPower = 5,
        baseSpeedMultiplier = 1.1f,
        abilityName = null,
        abilityDescription = "Specializes in Blue Fields (+50% Blue Pollen).",
        abilityType = AbilityType.BLUE_FIELD_BOOST,
        abilityValue = 0.5f,
        primaryColor = Color(0xFF81D4FA),
        stripeColor = Color(0xFF1565C0),
        wingColor = Color(0xFFE0F7FA),
        eyeColor = Color(0xFF0D47A1)
    ),
    RAD_BEE(
        id = "rad_bee",
        beeName = "Rad Bee",
        rarity = BeeRarity.UNCOMMON,
        description = "Radiates warm red energy and thrives in spicy rose fields.",
        baseGatherPower = 5,
        baseSpeedMultiplier = 1.1f,
        abilityName = null,
        abilityDescription = "Specializes in Red Fields (+50% Red Pollen).",
        abilityType = AbilityType.RED_FIELD_BOOST,
        abilityValue = 0.5f,
        primaryColor = Color(0xFFFF8A80),
        stripeColor = Color(0xFFC62828),
        wingColor = Color(0xFFFFEBEE),
        eyeColor = Color(0xFFB71C1C)
    ),
    BOMBER_BEE(
        id = "bomber_bee",
        beeName = "Bomber Bee",
        rarity = BeeRarity.UNCOMMON,
        description = "Carries miniature pollen powder kegs that pop for bursts of pollen.",
        baseGatherPower = 6,
        baseSpeedMultiplier = 1.0f,
        abilityName = null,
        abilityDescription = "Harvests with small explosive pollen bursts.",
        abilityType = AbilityType.POLLEN_BURST,
        abilityValue = 1.2f,
        primaryColor = Color(0xFFB0BEC5),
        stripeColor = Color(0xFF37474F),
        wingColor = Color(0xFFECEFF1)
    ),

    // 3. Rare (5 Types) - Abilities begin at Rare
    HASTY_BEE(
        id = "hasty_bee",
        beeName = "Hasty Bee",
        rarity = BeeRarity.RARE,
        description = "A whirlwind of perpetual motion that accelerates the player's boots.",
        baseGatherPower = 8,
        baseSpeedMultiplier = 1.6f,
        abilityName = "Swift Buzz",
        abilityDescription = "Increases player movement speed by +35%.",
        abilityType = AbilityType.PLAYER_SPEED_BOOST,
        abilityValue = 0.35f,
        primaryColor = Color(0xFFFFEE58),
        stripeColor = Color(0xFF00ACC1),
        wingColor = Color(0xFFE0F2F1)
    ),
    LOOKER_BEE(
        id = "looker_bee",
        beeName = "Looker Bee",
        rarity = BeeRarity.RARE,
        description = "Equipped with observant binoculars to spot rare nectar clusters.",
        baseGatherPower = 9,
        baseSpeedMultiplier = 1.2f,
        abilityName = "Focus Glare",
        abilityDescription = "+25% chance for Critical Pollen Gather (2.5x yield).",
        abilityType = AbilityType.CRITICAL_CHANCE,
        abilityValue = 0.25f,
        primaryColor = Color(0xFFCE93D8),
        stripeColor = Color(0xFF4A148C),
        wingColor = Color(0xFFF3E5F5)
    ),
    STUBBORN_BEE(
        id = "stubborn_bee",
        beeName = "Stubborn Bee",
        rarity = BeeRarity.RARE,
        description = "Heavier than most bees, refusing to leave a flower until it's emptied.",
        baseGatherPower = 12,
        baseSpeedMultiplier = 0.9f,
        abilityName = "Heavy Scoop",
        abilityDescription = "Grants +40% extra pollen gathered per swing.",
        abilityType = AbilityType.POLLEN_MULTIPLIER,
        abilityValue = 0.40f,
        primaryColor = Color(0xFFBCAAA4),
        stripeColor = Color(0xFF3E2723),
        wingColor = Color(0xFFEFEBE9)
    ),
    COMMANDER_BEE(
        id = "commander_bee",
        beeName = "Commander Bee",
        rarity = BeeRarity.RARE,
        description = "A natural hive leader whose marching calls embolden all swarm allies.",
        baseGatherPower = 10,
        baseSpeedMultiplier = 1.2f,
        abilityName = "Hive Aura",
        abilityDescription = "Boosts base gather stats of all equipped bees by +20%.",
        abilityType = AbilityType.SWARM_BUFF,
        abilityValue = 0.20f,
        primaryColor = Color(0xFFFFB74D),
        stripeColor = Color(0xFFBF360C),
        wingColor = Color(0xFFFFF3E0)
    ),
    HONEY_BEE(
        id = "honey_bee",
        beeName = "Honey Bee",
        rarity = BeeRarity.RARE,
        description = "A bee made purely of liquid honey. Turns raw nectar into liquid gold.",
        baseGatherPower = 11,
        baseSpeedMultiplier = 1.1f,
        abilityName = "Nectar Surge",
        abilityDescription = "Accelerates hive honey conversion speed by +50%.",
        abilityType = AbilityType.CONVERT_SPEED_BOOST,
        abilityValue = 0.50f,
        primaryColor = Color(0xFFFFD54F),
        stripeColor = Color(0xFFFF8F00),
        wingColor = Color(0xFFFFF8E1)
    ),

    // 4. Epic (2 Types)
    RAGE_BEE(
        id = "rage_bee",
        beeName = "Rage Bee",
        rarity = BeeRarity.EPIC,
        description = "Fiery rage pulses through its stinger, quadrupling gather output.",
        baseGatherPower = 22,
        baseSpeedMultiplier = 1.4f,
        abilityName = "Fury Stinger",
        abilityDescription = "+100% total pollen harvest power boost.",
        abilityType = AbilityType.POLLEN_MULTIPLIER,
        abilityValue = 1.0f,
        primaryColor = Color(0xFFFF5252),
        stripeColor = Color(0xFF212121),
        wingColor = Color(0xFFFFCDD2)
    ),
    BUBBLE_BEE(
        id = "bubble_bee",
        beeName = "Bubble Bee",
        rarity = BeeRarity.EPIC,
        description = "Summons iridescent floating bubbles that vacuum up surrounding flower pollen.",
        baseGatherPower = 20,
        baseSpeedMultiplier = 1.3f,
        abilityName = "Bubble Pop",
        abilityDescription = "Spawns pollen bubbles granting +80% area collection bonus.",
        abilityType = AbilityType.POLLEN_BURST,
        abilityValue = 0.80f,
        primaryColor = Color(0xFF4FC3F7),
        stripeColor = Color(0xFF0288D1),
        wingColor = Color(0xFFE1F5FE)
    ),

    // 5. Legendary (1 Type)
    MUSIC_BEE(
        id = "music_bee",
        beeName = "Music Bee",
        rarity = BeeRarity.LEGENDARY,
        description = "Plays mesmerizing melodies that double swarm focus and critical strikes.",
        baseGatherPower = 40,
        baseSpeedMultiplier = 1.5f,
        abilityName = "Melody Surge",
        abilityDescription = "Grants +50% gather speed and +50% critical strike chance.",
        abilityType = AbilityType.CRITICAL_CHANCE,
        abilityValue = 0.50f,
        primaryColor = Color(0xFFFFB300),
        stripeColor = Color(0xFF8E24AA),
        wingColor = Color(0xFFF3E5F5)
    ),

    // 6. Mythic (3 Types)
    FUZZY_BEE(
        id = "fuzzy_bee",
        beeName = "Fuzzy Bee",
        rarity = BeeRarity.MYTHIC,
        description = "Coat so densely fluffy that passing by flowers causes immediate super-blooms.",
        baseGatherPower = 80,
        baseSpeedMultiplier = 1.4f,
        abilityName = "Pollen Fuzz",
        abilityDescription = "Super-pollinates field flowers, making them yield 2.5x pollen.",
        abilityType = AbilityType.POLLEN_MULTIPLIER,
        abilityValue = 1.5f,
        primaryColor = Color(0xFFF06292),
        stripeColor = Color(0xFF880E4F),
        wingColor = Color(0xFFFCE4EC)
    ),
    VECTOR_BEE(
        id = "vector_bee",
        beeName = "Vector Bee",
        rarity = BeeRarity.MYTHIC,
        description = "Darting along sharp mathematical vectors, gathering multiple flowers at once.",
        baseGatherPower = 85,
        baseSpeedMultiplier = 1.9f,
        abilityName = "Triangulate",
        abilityDescription = "Gathers all flowers across triangular field coordinates (+180% yield).",
        abilityType = AbilityType.POLLEN_BURST,
        abilityValue = 1.8f,
        primaryColor = Color(0xFFBA68C8),
        stripeColor = Color(0xFF4A148C),
        wingColor = Color(0xFFEDE7F6)
    ),
    SPICY_BEE(
        id = "spicy_bee",
        beeName = "Spicy Bee",
        rarity = BeeRarity.MYTHIC,
        description = "Blazing thermal honey core scorches fields into ultra-concentrated amber.",
        baseGatherPower = 95,
        baseSpeedMultiplier = 1.6f,
        abilityName = "Inferno Flame",
        abilityDescription = "Field-wide fiery pollen surge (+200% pollen and +50% conversion).",
        abilityType = AbilityType.POLLEN_MULTIPLIER,
        abilityValue = 2.0f,
        primaryColor = Color(0xFFFF7043),
        stripeColor = Color(0xFFBF360C),
        wingColor = Color(0xFFFFCCBC)
    ),

    // 7. Divine (2 Types)
    PHOTON_BEE(
        id = "photon_bee",
        beeName = "Photon Bee",
        rarity = BeeRarity.DIVINE,
        description = "A living beam of pure sunlight that vaporizes pollen directly into your backpack.",
        baseGatherPower = 200,
        baseSpeedMultiplier = 2.0f,
        abilityName = "Beam of Light",
        abilityDescription = "Fires solar laser sweeps yielding +500% instant pollen.",
        abilityType = AbilityType.POLLEN_BURST,
        abilityValue = 5.0f,
        primaryColor = Color(0xFFFFF59D),
        stripeColor = Color(0xFFF57F17),
        wingColor = Color(0xFFFFFDE7)
    ),
    TABBY_BEE(
        id = "tabby_bee",
        beeName = "Tabby Bee",
        rarity = BeeRarity.DIVINE,
        description = "A divine feline bee whose affection and scratch power compound forever.",
        baseGatherPower = 220,
        baseSpeedMultiplier = 1.9f,
        abilityName = "Scratch & Purr",
        abilityDescription = "Permanent stacking gather multiplier (+300% total efficiency).",
        abilityType = AbilityType.SWARM_BUFF,
        abilityValue = 3.0f,
        primaryColor = Color(0xFFFFCA28),
        stripeColor = Color(0xFF6D4C41),
        wingColor = Color(0xFFEFEBE9)
    ),

    // 8. Celestial (1 Type)
    NEBULA_BEE(
        id = "nebula_bee",
        beeName = "Nebula Bee",
        rarity = BeeRarity.CELESTIAL,
        description = "Born within a cosmic stellar nursery, drawing cosmic star-honey from the void.",
        baseGatherPower = 500,
        baseSpeedMultiplier = 2.4f,
        abilityName = "Cosmic Singularity",
        abilityDescription = "Creates a cosmic pollen vortex granting +1000% harvest yield.",
        abilityType = AbilityType.POLLEN_MULTIPLIER,
        abilityValue = 10.0f,
        primaryColor = Color(0xFF4DD0E1),
        stripeColor = Color(0xFF311B92),
        wingColor = Color(0xFFE0F7FA)
    ),

    // 9. Secret (1 Type) - Extremely rare 1 in 100,000!
    CHRONO_GOD_BEE(
        id = "chrono_god_bee",
        beeName = "Chrono God Bee",
        rarity = BeeRarity.SECRET,
        description = "The supreme ancient master of time and honey. Bends reality to flood the world with honey.",
        baseGatherPower = 1500,
        baseSpeedMultiplier = 3.0f,
        abilityName = "Time Distortion",
        abilityDescription = "Time freezes while all pollen, honey, and bond gains are multiplied by 10x!",
        abilityType = AbilityType.GOD_DISTORTION,
        abilityValue = 10.0f,
        primaryColor = Color(0xFFFF1744),
        stripeColor = Color(0xFF00E676),
        wingColor = Color(0xFF18FFFF)
    );

    companion object {
        fun byId(id: String): BeeType {
            return entries.find { it.id == id } ?: BASIC_BEE
        }
    }
}

enum class AbilityType {
    NONE,
    PLAYER_SPEED_BOOST,
    CRITICAL_CHANCE,
    POLLEN_MULTIPLIER,
    POLLEN_BURST,
    SWARM_BUFF,
    CONVERT_SPEED_BOOST,
    BLUE_FIELD_BOOST,
    RED_FIELD_BOOST,
    GOD_DISTORTION
}
