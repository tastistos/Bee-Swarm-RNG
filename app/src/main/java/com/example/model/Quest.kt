package com.example.model

enum class QuestObjectiveType {
    COLLECT_POLLEN,
    CONVERT_HONEY,
    FEED_TREATS,
    LEVEL_UP_BEE,
    BUY_SLOT,
    ROLL_BEE,
    OWN_BEES_COUNT
}

data class Quest(
    val id: String,
    val npcId: String,
    val title: String,
    val description: String,
    val objectiveType: QuestObjectiveType,
    val targetAmount: Long,
    var currentProgress: Long = 0,
    val rewardHoney: Long,
    val rewardTreats: Int = 0,
    val rewardRoyalJelly: Int = 0,
    val rewardGoldenEggs: Int = 0,
    var isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = currentProgress >= targetAmount

    val progressFraction: Float
        get() = if (targetAmount > 0) (currentProgress.toFloat() / targetAmount).coerceIn(0f, 1f) else 1f
}
