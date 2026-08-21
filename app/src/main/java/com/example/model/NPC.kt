package com.example.model

import androidx.compose.ui.geometry.Offset

data class NPC(
    val id: String,
    val name: String,
    val title: String,
    val greeting: String,
    val position: Offset,
    val emoji: String,
    val questIds: List<String>
) {
    companion object {
        val BLACK_BEAR = NPC(
            id = "npc_black_bear",
            name = "Black Bear",
            title = "Field Mentor",
            greeting = "Welcome to the meadow, young beekeeper! Scoop some pollen and bring it back to your hive for golden honey.",
            position = Offset(820f, 920f),
            emoji = "🐻",
            questIds = listOf("quest_bb_1", "quest_bb_2", "quest_bb_3", "quest_bb_4")
        )

        val MOTHER_BEAR = NPC(
            id = "npc_mother_bear",
            name = "Mother Bear",
            title = "Nurturer of Swarms",
            greeting = "Hello sweetie! Happy bees are strong bees. Feed your bees Treats to boost their bond and level them up!",
            position = Offset(520f, 540f),
            emoji = "🧸",
            questIds = listOf("quest_mb_1", "quest_mb_2", "quest_mb_3")
        )

        val PANDA_BEAR = NPC(
            id = "npc_panda_bear",
            name = "Panda Bear",
            title = "Flower Master",
            greeting = "Greetings! Each flower color holds different pollen virtues. Explore the Rose and Blue fields to master the meadow.",
            position = Offset(1500f, 480f),
            emoji = "🐼",
            questIds = listOf("quest_pb_1", "quest_pb_2")
        )

        val SCIENCE_BEAR = NPC(
            id = "npc_science_bear",
            name = "Science Bear",
            title = "RNG Geneticist",
            greeting = "Fascinating! The genetic potential of bees is limitless! Spin the RNG eggs and test your luck for Divine and Secret bees!",
            position = Offset(1480f, 1380f),
            emoji = "🔬",
            questIds = listOf("quest_sb_1", "quest_sb_2")
        )

        val ALL_NPCS = listOf(BLACK_BEAR, MOTHER_BEAR, PANDA_BEAR, SCIENCE_BEAR)

        fun getDefaultQuests(): List<Quest> {
            return listOf(
                Quest(
                    id = "quest_bb_1",
                    npcId = "npc_black_bear",
                    title = "First Pollen Scoop",
                    description = "Collect 50 pollen from the Dandelion Field using your scoop.",
                    objectiveType = QuestObjectiveType.COLLECT_POLLEN,
                    targetAmount = 50L,
                    rewardHoney = 250L,
                    rewardTreats = 10
                ),
                Quest(
                    id = "quest_bb_2",
                    npcId = "npc_black_bear",
                    title = "Liquid Gold",
                    description = "Convert 100 pollen into honey at your hive.",
                    objectiveType = QuestObjectiveType.CONVERT_HONEY,
                    targetAmount = 100L,
                    rewardHoney = 600L,
                    rewardTreats = 15,
                    rewardRoyalJelly = 1
                ),
                Quest(
                    id = "quest_bb_3",
                    npcId = "npc_black_bear",
                    title = "Hive Expansion",
                    description = "Purchase a 2nd bee slot for your hive.",
                    objectiveType = QuestObjectiveType.BUY_SLOT,
                    targetAmount = 2L,
                    rewardHoney = 1500L,
                    rewardTreats = 25,
                    rewardGoldenEggs = 1
                ),
                Quest(
                    id = "quest_bb_4",
                    npcId = "npc_black_bear",
                    title = "Swarm Growth",
                    description = "Own 3 or more bees in your collection.",
                    objectiveType = QuestObjectiveType.OWN_BEES_COUNT,
                    targetAmount = 3L,
                    rewardHoney = 5000L,
                    rewardTreats = 50,
                    rewardRoyalJelly = 3
                ),
                Quest(
                    id = "quest_mb_1",
                    npcId = "npc_mother_bear",
                    title = "Sweet Treats",
                    description = "Feed 10 Treats to your bees to build their bond.",
                    objectiveType = QuestObjectiveType.FEED_TREATS,
                    targetAmount = 10L,
                    rewardHoney = 800L,
                    rewardTreats = 30
                ),
                Quest(
                    id = "quest_mb_2",
                    npcId = "npc_mother_bear",
                    title = "Rising Stars",
                    description = "Raise any bee to Level 3 or higher.",
                    objectiveType = QuestObjectiveType.LEVEL_UP_BEE,
                    targetAmount = 3L,
                    rewardHoney = 2000L,
                    rewardTreats = 40,
                    rewardRoyalJelly = 2
                ),
                Quest(
                    id = "quest_mb_3",
                    npcId = "npc_mother_bear",
                    title = "Master Bond",
                    description = "Feed 50 total Treats to your bees.",
                    objectiveType = QuestObjectiveType.FEED_TREATS,
                    targetAmount = 50L,
                    rewardHoney = 8000L,
                    rewardTreats = 100,
                    rewardGoldenEggs = 1
                ),
                Quest(
                    id = "quest_pb_1",
                    npcId = "npc_panda_bear",
                    title = "Pollen Harvester",
                    description = "Harvest a total of 1,000 pollen across all fields.",
                    objectiveType = QuestObjectiveType.COLLECT_POLLEN,
                    targetAmount = 1000L,
                    rewardHoney = 3500L,
                    rewardTreats = 35
                ),
                Quest(
                    id = "quest_pb_2",
                    npcId = "npc_panda_bear",
                    title = "Honey Empire",
                    description = "Convert 5,000 total honey at your hive.",
                    objectiveType = QuestObjectiveType.CONVERT_HONEY,
                    targetAmount = 5000L,
                    rewardHoney = 12000L,
                    rewardTreats = 60,
                    rewardRoyalJelly = 5
                ),
                Quest(
                    id = "quest_sb_1",
                    npcId = "npc_science_bear",
                    title = "Genetic Experiments",
                    description = "Hatch 3 bees using the RNG egg opening system.",
                    objectiveType = QuestObjectiveType.ROLL_BEE,
                    targetAmount = 3L,
                    rewardHoney = 2500L,
                    rewardRoyalJelly = 3
                ),
                Quest(
                    id = "quest_sb_2",
                    npcId = "npc_science_bear",
                    title = "Quantum Swarm",
                    description = "Hatch 10 total bees through RNG.",
                    objectiveType = QuestObjectiveType.ROLL_BEE,
                    targetAmount = 10L,
                    rewardHoney = 15000L,
                    rewardGoldenEggs = 2,
                    rewardTreats = 150
                )
            )
        }
    }
}
