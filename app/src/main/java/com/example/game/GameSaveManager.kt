package com.example.game

import android.content.Context
import com.example.model.BeeInstance
import com.example.model.BeeType
import com.example.model.NPC
import com.example.model.Quest
import org.json.JSONArray
import org.json.JSONObject

class GameSaveManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("bee_swarm_rng_save", Context.MODE_PRIVATE)

    fun saveGame(state: GameState) {
        try {
            val json = JSONObject()
            json.put("honey", state.honey)
            json.put("pollen", state.pollen)
            json.put("hiveSlots", state.hiveSlots)
            json.put("toolId", state.toolId)
            json.put("backpackId", state.backpackId)
            json.put("accessoryId", state.accessoryId ?: "")
            json.put("tutorialStep", state.tutorialStep)
            json.put("isTutorialCompleted", state.isTutorialCompleted)
            json.put("totalPollenCollected", state.totalPollenCollected)
            json.put("totalHoneyConverted", state.totalHoneyConverted)
            json.put("totalRollsCount", state.totalRollsCount)

            // Owned Equipment
            val eqArray = JSONArray()
            for (id in state.ownedEquipmentIds) eqArray.put(id)
            json.put("ownedEquipment", eqArray)

            // Items
            val itemsObj = JSONObject()
            for ((k, v) in state.items) itemsObj.put(k, v)
            json.put("items", itemsObj)

            // Discovered Bees
            val discArray = JSONArray()
            for (id in state.discoveredBeeIds) discArray.put(id)
            json.put("discoveredBees", discArray)

            // Owned Bees
            val beesArray = JSONArray()
            for (bee in state.ownedBees) {
                val bObj = JSONObject()
                bObj.put("instanceId", bee.instanceId)
                bObj.put("beeTypeId", bee.beeType.id)
                bObj.put("level", bee.level)
                bObj.put("currentBond", bee.currentBond)
                bObj.put("isEquipped", bee.isEquipped)
                bObj.put("slotIndex", bee.slotIndex)
                beesArray.put(bObj)
            }
            json.put("ownedBees", beesArray)

            // Quests
            val questsArray = JSONArray()
            for (q in state.quests) {
                val qObj = JSONObject()
                qObj.put("id", q.id)
                qObj.put("progress", q.currentProgress)
                qObj.put("isClaimed", q.isClaimed)
                questsArray.put(qObj)
            }
            json.put("quests", questsArray)

            prefs.edit().putString("save_data_v1", json.toString()).apply()
        } catch (_: Exception) {}
    }

    fun loadGame(): GameState {
        val raw = prefs.getString("save_data_v1", null) ?: return GameState()
        return try {
            val json = JSONObject(raw)
            val state = GameState(
                honey = json.optLong("honey", 100L),
                pollen = json.optLong("pollen", 0L),
                hiveSlots = json.optInt("hiveSlots", 1),
                toolId = json.optString("toolId", "tool_plastic_scoop"),
                backpackId = json.optString("backpackId", "bag_pouch"),
                accessoryId = json.optString("accessoryId").ifBlank { null },
                tutorialStep = json.optInt("tutorialStep", 1),
                isTutorialCompleted = json.optBoolean("isTutorialCompleted", false),
                totalPollenCollected = json.optLong("totalPollenCollected", 0L),
                totalHoneyConverted = json.optLong("totalHoneyConverted", 0L),
                totalRollsCount = json.optLong("totalRollsCount", 0L)
            )

            // Owned Equipment
            val eqArray = json.optJSONArray("ownedEquipment")
            if (eqArray != null) {
                state.ownedEquipmentIds.clear()
                for (i in 0 until eqArray.length()) {
                    state.ownedEquipmentIds.add(eqArray.getString(i))
                }
            }

            // Items
            val itemsObj = json.optJSONObject("items")
            if (itemsObj != null) {
                state.items.clear()
                val keys = itemsObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    state.items[k] = itemsObj.optInt(k, 0)
                }
            }

            // Discovered Bees
            val discArray = json.optJSONArray("discoveredBees")
            if (discArray != null) {
                state.discoveredBeeIds.clear()
                for (i in 0 until discArray.length()) {
                    state.discoveredBeeIds.add(discArray.getString(i))
                }
            }

            // Owned Bees
            val beesArray = json.optJSONArray("ownedBees")
            if (beesArray != null && beesArray.length() > 0) {
                state.ownedBees.clear()
                for (i in 0 until beesArray.length()) {
                    val bObj = beesArray.getJSONObject(i)
                    val beeType = BeeType.byId(bObj.getString("beeTypeId"))
                    val bee = BeeInstance(
                        instanceId = bObj.optString("instanceId"),
                        beeType = beeType,
                        level = bObj.optInt("level", 1),
                        currentBond = bObj.optInt("currentBond", 0),
                        isEquipped = bObj.optBoolean("isEquipped", false),
                        slotIndex = bObj.optInt("slotIndex", -1)
                    )
                    state.ownedBees.add(bee)
                }
            }

            // Quests
            val questsArray = json.optJSONArray("quests")
            val defaultQuests = NPC.getDefaultQuests().associateBy { it.id }.toMutableMap()
            if (questsArray != null) {
                for (i in 0 until questsArray.length()) {
                    val qObj = questsArray.getJSONObject(i)
                    val qId = qObj.optString("id")
                    val existing = defaultQuests[qId]
                    if (existing != null) {
                        existing.currentProgress = qObj.optLong("progress", 0L)
                        existing.isClaimed = qObj.optBoolean("isClaimed", false)
                    }
                }
            }
            state.quests.clear()
            state.quests.addAll(defaultQuests.values)

            state
        } catch (e: Exception) {
            GameState()
        }
    }

    fun clearSave() {
        prefs.edit().clear().apply()
    }
}
