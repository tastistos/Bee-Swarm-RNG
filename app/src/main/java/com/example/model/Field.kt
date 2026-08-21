package com.example.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

enum class FlowerColor(val color: Color, val hex: Long) {
    WHITE(Color(0xFFF5F5F5), 0xFFF5F5F5),
    BLUE(Color(0xFF42A5F5), 0xFF42A5F5),
    RED(Color(0xFFEF5350), 0xFFEF5350),
    YELLOW(Color(0xFFFFEE58), 0xFFFFEE58),
    GOLD(Color(0xFFFFD54F), 0xFFFFD54F),
    COSMIC(Color(0xFF00E5FF), 0xFF00E5FF)
}

data class Field(
    val id: String,
    val name: String,
    val bounds: Rect,
    val groundColor: Color,
    val primaryFlowerColor: FlowerColor,
    val pollenMultiplier: Float,
    val flowerCount: Int = 30
) {
    companion object {
        // Map layout: World is 2400 x 1600 units
        val HIVE_ZONE = Field(
            id = "field_hive_zone",
            name = "Hive Sanctuary",
            bounds = Rect(200f, 600f, 750f, 1150f),
            groundColor = Color(0xFF558B2F),
            primaryFlowerColor = FlowerColor.YELLOW,
            pollenMultiplier = 1.0f,
            flowerCount = 8
        )

        val DANDELION_FIELD = Field(
            id = "field_dandelion",
            name = "Dandelion Field",
            bounds = Rect(900f, 750f, 1450f, 1250f),
            groundColor = Color(0xFF689F38),
            primaryFlowerColor = FlowerColor.WHITE,
            pollenMultiplier = 1.0f,
            flowerCount = 36
        )

        val CLOVER_FIELD = Field(
            id = "field_clover",
            name = "Clover Field",
            bounds = Rect(1550f, 750f, 2100f, 1250f),
            groundColor = Color(0xFF43A047),
            primaryFlowerColor = FlowerColor.YELLOW,
            pollenMultiplier = 1.5f,
            flowerCount = 40
        )

        val BLUE_FLOWER_FIELD = Field(
            id = "field_blue_flower",
            name = "Blue Flower Field",
            bounds = Rect(900f, 200f, 1450f, 650f),
            groundColor = Color(0xFF388E3C),
            primaryFlowerColor = FlowerColor.BLUE,
            pollenMultiplier = 2.0f,
            flowerCount = 40
        )

        val ROSE_FIELD = Field(
            id = "field_rose",
            name = "Rose Field",
            bounds = Rect(1550f, 200f, 2100f, 650f),
            groundColor = Color(0xFF2E7D32),
            primaryFlowerColor = FlowerColor.RED,
            pollenMultiplier = 3.0f,
            flowerCount = 45
        )

        val SUNFLOWER_FIELD = Field(
            id = "field_sunflower",
            name = "Sunflower Field",
            bounds = Rect(250f, 150f, 750f, 550f),
            groundColor = Color(0xFF558B2F),
            primaryFlowerColor = FlowerColor.GOLD,
            pollenMultiplier = 4.0f,
            flowerCount = 35
        )

        val MOUNTAIN_TOP_FIELD = Field(
            id = "field_mountain_top",
            name = "Mountain Peak Shrine",
            bounds = Rect(950f, 1320f, 1950f, 1580f),
            groundColor = Color(0xFF1B5E20),
            primaryFlowerColor = FlowerColor.COSMIC,
            pollenMultiplier = 8.0f,
            flowerCount = 50
        )

        val ALL_FIELDS = listOf(
            HIVE_ZONE, DANDELION_FIELD, CLOVER_FIELD, BLUE_FLOWER_FIELD,
            ROSE_FIELD, SUNFLOWER_FIELD, MOUNTAIN_TOP_FIELD
        )
    }
}
