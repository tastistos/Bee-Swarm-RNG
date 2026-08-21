package com.example.model

enum class ItemType(
    val id: String,
    val itemName: String,
    val description: String,
    val emoji: String,
    val isUsable: Boolean
) {
    TREAT(
        id = "item_treat",
        itemName = "Treat",
        description = "A sweet sugary snack. Gives +10 Bond when fed to a bee.",
        emoji = "🍬",
        isUsable = false // Used directly in Bee feeding UI
    ),
    ROYAL_JELLY(
        id = "item_royal_jelly",
        itemName = "Royal Jelly",
        description = "Transforms a bee or rolls for a new bee of at least Rare quality with higher luck!",
        emoji = "🍯",
        isUsable = true
    ),
    GOLDEN_EGG(
        id = "item_golden_egg",
        itemName = "Golden Egg",
        description = "A shimmering golden egg guaranteed to hatch an Epic, Legendary, Mythic, or higher bee!",
        emoji = "🥚",
        isUsable = true
    ),
    HONEY_POTION(
        id = "item_honey_potion",
        itemName = "Honey Potion",
        description = "Instantly converts all backpack pollen to honey anywhere on the map!",
        emoji = "🧪",
        isUsable = true
    ),
    SUPER_SMOOTHIE(
        id = "item_super_smoothie",
        itemName = "Super Smoothie",
        description = "Boosts player move speed and bee gather rates by +100% for 60 seconds.",
        emoji = "🥤",
        isUsable = true
    );

    companion object {
        fun byId(id: String): ItemType {
            return entries.find { it.id == id } ?: TREAT
        }
    }
}
