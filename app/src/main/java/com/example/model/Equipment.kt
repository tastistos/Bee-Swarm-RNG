package com.example.model

enum class EquipmentType {
    TOOL,
    BACKPACK,
    ACCESSORY
}

data class Equipment(
    val id: String,
    val name: String,
    val type: EquipmentType,
    val description: String,
    val costHoney: Long,
    val pollenPerGather: Int = 0,
    val gatherCooldownMs: Long = 600,
    val pollenCapacity: Long = 0,
    val moveSpeedBonus: Float = 0f,
    val convertSpeedBonus: Float = 0f,
    val critChanceBonus: Float = 0f,
    val iconEmoji: String
) {
    companion object {
        // Tools
        val PLASTIC_SCOOP = Equipment(
            id = "tool_plastic_scoop",
            name = "Plastic Scoop",
            type = EquipmentType.TOOL,
            description = "A simple plastic gardening scoop for starter gatherers.",
            costHoney = 0L,
            pollenPerGather = 2,
            gatherCooldownMs = 500,
            iconEmoji = "🥄"
        )
        val RAKE = Equipment(
            id = "tool_rake",
            name = "Rake",
            type = EquipmentType.TOOL,
            description = "Sweeps through flower beds, gathering multiple petals.",
            costHoney = 500L,
            pollenPerGather = 6,
            gatherCooldownMs = 450,
            iconEmoji = "🧹"
        )
        val CLIPPERS = Equipment(
            id = "tool_clippers",
            name = "Clippers",
            type = EquipmentType.TOOL,
            description = "Sharp shears that trim flowers cleanly for rich nectar.",
            costHoney = 3500L,
            pollenPerGather = 16,
            gatherCooldownMs = 400,
            iconEmoji = "✂️"
        )
        val GOLDEN_RAKE = Equipment(
            id = "tool_golden_rake",
            name = "Golden Rake",
            type = EquipmentType.TOOL,
            description = "Gleaming golden rake that multiplies pollen yield.",
            costHoney = 25000L,
            pollenPerGather = 45,
            gatherCooldownMs = 350,
            critChanceBonus = 0.10f,
            iconEmoji = "🔱"
        )
        val SPARK_STAFF = Equipment(
            id = "tool_spark_staff",
            name = "Spark Staff",
            type = EquipmentType.TOOL,
            description = "Electrified wand sending arcs of gathering energy.",
            costHoney = 150000L,
            pollenPerGather = 130,
            gatherCooldownMs = 300,
            critChanceBonus = 0.20f,
            iconEmoji = "⚡"
        )
        val PORCELAIN_DIPPER = Equipment(
            id = "tool_porcelain_dipper",
            name = "Porcelain Dipper",
            type = EquipmentType.TOOL,
            description = "A massive fine ceramic dipper with immense suction.",
            costHoney = 1000000L,
            pollenPerGather = 400,
            gatherCooldownMs = 250,
            critChanceBonus = 0.30f,
            iconEmoji = "🏺"
        )
        val TIDE_POPPER = Equipment(
            id = "tool_tide_popper",
            name = "Tide Popper",
            type = EquipmentType.TOOL,
            description = "End-game legendary bubble cannon harvesting whole fields.",
            costHoney = 10000000L,
            pollenPerGather = 1500,
            gatherCooldownMs = 200,
            critChanceBonus = 0.50f,
            iconEmoji = "🌊"
        )

        // Backpacks
        val POUCH = Equipment(
            id = "bag_pouch",
            name = "Pouch",
            type = EquipmentType.BACKPACK,
            description = "A humble cloth pouch.",
            costHoney = 0L,
            pollenCapacity = 100L,
            iconEmoji = "👝"
        )
        val SMALL_JAR = Equipment(
            id = "bag_small_jar",
            name = "Glass Jar",
            type = EquipmentType.BACKPACK,
            description = "A transparent jar to store more pollen.",
            costHoney = 400L,
            pollenCapacity = 400L,
            iconEmoji = "🫙"
        )
        val BACKPACK = Equipment(
            id = "bag_backpack",
            name = "Canvas Backpack",
            type = EquipmentType.BACKPACK,
            description = "Sturdy backpack with dual side pockets.",
            costHoney = 2500L,
            pollenCapacity = 1500L,
            iconEmoji = "🎒"
        )
        val MEGA_JUG = Equipment(
            id = "bag_mega_jug",
            name = "Mega Jug",
            type = EquipmentType.BACKPACK,
            description = "Reinforced barrel jug that holds copious amounts of pollen.",
            costHoney = 15000L,
            pollenCapacity = 6000L,
            iconEmoji = "🛢️"
        )
        val PORT_O_HIVE = Equipment(
            id = "bag_port_o_hive",
            name = "Port-O-Hive",
            type = EquipmentType.BACKPACK,
            description = "Mini portable hive backpack with built-in nectar filters.",
            costHoney = 100000L,
            pollenCapacity = 25000L,
            convertSpeedBonus = 0.25f,
            iconEmoji = "📦"
        )
        val PORCELAIN_BAG = Equipment(
            id = "bag_porcelain_bag",
            name = "Porcelain Bag",
            type = EquipmentType.BACKPACK,
            description = "Pristine white porcelain canister of vast capacity.",
            costHoney = 1500000L,
            pollenCapacity = 120000L,
            convertSpeedBonus = 0.50f,
            iconEmoji = "💎"
        )
        val COCONUT_CANISTER = Equipment(
            id = "bag_coconut_canister",
            name = "Coconut Canister",
            type = EquipmentType.BACKPACK,
            description = "Colossal high-tech coconut storing millions of pollen.",
            costHoney = 25000000L,
            pollenCapacity = 1000000L,
            convertSpeedBonus = 1.0f,
            iconEmoji = "🥥"
        )

        // Accessories
        val RUNNER_BOOTS = Equipment(
            id = "acc_runner_boots",
            name = "Runner Boots",
            type = EquipmentType.ACCESSORY,
            description = "Lightweight sneakers that increase walk speed.",
            costHoney = 1200L,
            moveSpeedBonus = 0.25f,
            iconEmoji = "👟"
        )
        val HONEY_BELT = Equipment(
            id = "acc_honey_belt",
            name = "Honey Belt",
            type = EquipmentType.ACCESSORY,
            description = "Gilded utility belt that speeds up honey conversion.",
            costHoney = 8000L,
            convertSpeedBonus = 0.40f,
            iconEmoji = "🥋"
        )
        val BEEKEEPER_MASK = Equipment(
            id = "acc_beekeeper_mask",
            name = "Beekeeper Mask",
            type = EquipmentType.ACCESSORY,
            description = "Protective mesh that grants critical gathering vision.",
            costHoney = 75000L,
            critChanceBonus = 0.25f,
            iconEmoji = "🎭"
        )

        val ALL_EQUIPMENT = listOf(
            PLASTIC_SCOOP, RAKE, CLIPPERS, GOLDEN_RAKE, SPARK_STAFF, PORCELAIN_DIPPER, TIDE_POPPER,
            POUCH, SMALL_JAR, BACKPACK, MEGA_JUG, PORT_O_HIVE, PORCELAIN_BAG, COCONUT_CANISTER,
            RUNNER_BOOTS, HONEY_BELT, BEEKEEPER_MASK
        )

        fun getById(id: String): Equipment {
            return ALL_EQUIPMENT.find { it.id == id } ?: PLASTIC_SCOOP
        }
    }
}
