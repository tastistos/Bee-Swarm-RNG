package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.audio.GameAudio
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*
import kotlin.random.Random

class GameEngine(
    val saveManager: GameSaveManager
) {
    var gameState: GameState = saveManager.loadGame()
        private set

    // Player position in the 2D world (World size is 2400x1600)
    // Starting spawn point in front of the hive
    var playerPos = Offset(520f, 920f)
        private set

    var playerFacingLeft = false
        private set

    var isSwingingScoop = false
        private set
    var scoopSwingAngle = 0f
        private set

    // Swarm bees in world
    val swarmEntities = mutableListOf<SwarmBeeEntity>()

    // Flowers in world
    val flowers = mutableListOf<FlowerState>()

    // Particles and floating numbers
    val floatingTexts = mutableListOf<FloatingText>()
    val particles = mutableListOf<Particle>()

    // UI state flows
    private val _isNearHive = MutableStateFlow(false)
    val isNearHive = _isNearHive.asStateFlow()

    private val _currentField = MutableStateFlow<Field?>(Field.HIVE_ZONE)
    val currentField = _currentField.asStateFlow()

    private val _isConvertingHoney = MutableStateFlow(false)
    val isConvertingHoney = _isConvertingHoney.asStateFlow()

    private val _lastGatherTimestamp = MutableStateFlow(0L)

    var worldTick = 0L
        private set

    val hiveCenter = Offset(450f, 850f)
    val hiveInteractionRadius = 180f

    init {
        initWorldFlowers()
        syncSwarmEntities()
    }

    fun initWorldFlowers() {
        flowers.clear()
        for (field in Field.ALL_FIELDS) {
            if (field.id == Field.HIVE_ZONE.id) continue
            val b = field.bounds
            val margin = 35f
            val count = field.flowerCount
            for (i in 0 until count) {
                val fx = b.left + margin + (Random.nextFloat() * (b.width - margin * 2))
                val fy = b.top + margin + (Random.nextFloat() * (b.height - margin * 2))
                flowers.add(
                    FlowerState(
                        fieldId = field.id,
                        position = Offset(fx, fy),
                        flowerColor = field.primaryFlowerColor,
                        pollenRemaining = 100f,
                        maxPollen = 100f
                    )
                )
            }
        }
    }

    fun syncSwarmEntities() {
        swarmEntities.clear()
        val equipped = gameState.equippedBees
        for ((index, bee) in equipped.withIndex()) {
            val angle = (index * (2 * Math.PI / (equipped.size.coerceAtLeast(1)))).toFloat()
            val offset = Offset(cos(angle) * 50f, sin(angle) * 50f)
            swarmEntities.add(
                SwarmBeeEntity(
                    instance = bee,
                    position = playerPos + offset,
                    wingPhase = Random.nextFloat() * 10f
                )
            )
        }
    }

    fun update(deltaSeconds: Float, joystickVector: Offset) {
        worldTick++

        // 1. Move Player
        val baseSpeed = 220f
        var speedMultiplier = 1.0f

        // Equipment speed bonus
        val accessory = gameState.accessoryId?.let { Equipment.getById(it) }
        speedMultiplier += accessory?.moveSpeedBonus ?: 0f

        // Bee passive speed boosts
        for (bee in gameState.equippedBees) {
            if (bee.beeType.abilityType == AbilityType.PLAYER_SPEED_BOOST) {
                speedMultiplier += bee.beeType.abilityValue
            }
        }

        if (joystickVector.getDistance() > 0.05f) {
            val move = joystickVector * (baseSpeed * speedMultiplier * deltaSeconds)
            val newX = (playerPos.x + move.x).coerceIn(60f, 2340f)
            val newY = (playerPos.y + move.y).coerceIn(60f, 1540f)
            playerPos = Offset(newX, newY)

            if (move.x < -0.1f) playerFacingLeft = true
            if (move.x > 0.1f) playerFacingLeft = false

            // Step 1 of tutorial check
            if (gameState.tutorialStep == 1 && isInField(Field.DANDELION_FIELD)) {
                gameState.tutorialStep = 2
            }
        }

        // 2. Check Field
        var foundField: Field? = null
        for (f in Field.ALL_FIELDS) {
            if (f.bounds.contains(playerPos)) {
                foundField = f
                break
            }
        }
        _currentField.value = foundField

        // 3. Hive Proximity Check
        val distToHive = (playerPos - hiveCenter).getDistance()
        val near = distToHive <= hiveInteractionRadius
        _isNearHive.value = near

        // 4. Update Scoop Swing Animation
        if (isSwingingScoop) {
            scoopSwingAngle += deltaSeconds * 900f
            if (scoopSwingAngle >= 140f) {
                isSwingingScoop = false
                scoopSwingAngle = 0f
            }
        }

        // 5. Honey Conversion
        if (_isConvertingHoney.value) {
            if (gameState.pollen > 0) {
                var convertRate = 35f * deltaSeconds * 10f
                val equipped = gameState.equippedBees
                for (bee in equipped) {
                    if (bee.beeType.abilityType == AbilityType.CONVERT_SPEED_BOOST) {
                        convertRate *= (1f + bee.beeType.abilityValue)
                    }
                }
                convertRate *= (1f + (gameState.currentBackpack.convertSpeedBonus))

                val toConvert = min(gameState.pollen, convertRate.toLong().coerceAtLeast(1L))
                gameState.pollen -= toConvert
                gameState.honey += toConvert
                gameState.totalHoneyConverted += toConvert
                trackQuestProgress(QuestObjectiveType.CONVERT_HONEY, toConvert)

                // Tutorial Step 3 Check
                if (gameState.tutorialStep == 3 && gameState.honey >= 100) {
                    gameState.tutorialStep = 4
                }

                // Chime sound and golden particles
                if (worldTick % 6 == 0L) {
                    GameAudio.playConvertHoney()
                    particles.add(
                        Particle(
                            position = hiveCenter + Offset(Random.nextFloat() * 40f - 20f, Random.nextFloat() * 40f - 20f),
                            velocity = Offset(Random.nextFloat() * 60f - 30f, -Random.nextFloat() * 80f - 30f),
                            color = Color(0xFFFFD54F),
                            size = 6f
                        )
                    )
                }

                if (gameState.pollen <= 0) {
                    _isConvertingHoney.value = false
                }
            } else {
                _isConvertingHoney.value = false
            }
        }

        // 6. Update Swarm Entities (Bees following player & gathering)
        for ((idx, beeEntity) in swarmEntities.withIndex()) {
            beeEntity.wingPhase += deltaSeconds * 28f * beeEntity.instance.effectiveSpeed

            // Target formation offset behind player
            val angle = (idx * (2 * Math.PI / swarmEntities.size.coerceAtLeast(1)) + worldTick * 0.03).toFloat()
            val orbitRadius = 45f + (idx % 3) * 15f
            val targetPos = playerPos + Offset(cos(angle) * orbitRadius, sin(angle) * orbitRadius - 20f)

            val dir = targetPos - beeEntity.position
            val dist = dir.getDistance()
            if (dist > 5f) {
                beeEntity.position += dir * (deltaSeconds * 4.5f * beeEntity.instance.effectiveSpeed)
            }

            // Passive bee flower gathering
            if (foundField != null && foundField.id != Field.HIVE_ZONE.id) {
                beeEntity.gatherTimerMs += (deltaSeconds * 1000).toLong()
                val gatherInterval = (1400 / beeEntity.instance.effectiveSpeed).toLong()
                if (beeEntity.gatherTimerMs >= gatherInterval) {
                    beeEntity.gatherTimerMs = 0
                    gatherFromNearbyFlower(beeEntity)
                }
            }
        }

        // 7. Update Flowers (Regeneration)
        for (flower in flowers) {
            if (flower.pollenRemaining < flower.maxPollen) {
                flower.pollenRemaining = min(flower.maxPollen, flower.pollenRemaining + deltaSeconds * 22f)
            }
        }

        // 8. Update Floating Texts & Particles
        val itText = floatingTexts.iterator()
        while (itText.hasNext()) {
            val t = itText.next()
            t.ageMs += (deltaSeconds * 1000).toLong()
            if (t.ageMs >= t.maxAgeMs) {
                itText.remove()
            }
        }

        val itPart = particles.iterator()
        while (itPart.hasNext()) {
            val p = itPart.next()
            p.ageMs += (deltaSeconds * 1000).toLong()
            p.position += p.velocity * deltaSeconds
            p.alpha = 1f - (p.ageMs.toFloat() / p.maxAgeMs).coerceIn(0f, 1f)
            if (p.ageMs >= p.maxAgeMs) {
                itPart.remove()
            }
        }

        // Periodic auto-save every 10 seconds
        if (worldTick % 600 == 0L) {
            saveManager.saveGame(gameState)
        }
    }

    fun gatherPlayerPollen(): Boolean {
        val tool = gameState.currentTool
        if (isSwingingScoop) return false
        if (gameState.pollen >= gameState.maxPollenCapacity) {
            addFloatingText("Backpack Full!", playerPos, Color(0xFFFF5252))
            return false
        }

        val curField = _currentField.value
        if (curField == null || curField.id == Field.HIVE_ZONE.id) {
            addFloatingText("Go to a flower field!", playerPos, Color(0xFFFFD54F))
            return false
        }

        isSwingingScoop = true
        scoopSwingAngle = -45f
        GameAudio.playGatherPollen()

        // Gather from flowers within radius 80f
        var gatheredTotal = 0L
        val gatherRadius = 90f
        var hits = 0

        for (flower in flowers) {
            if ((flower.position - playerPos).getDistance() <= gatherRadius && flower.pollenRemaining > 5f) {
                hits++
                val harvested = min(flower.pollenRemaining, 25f)
                flower.pollenRemaining -= harvested

                // Spawn pollen particles
                particles.add(
                    Particle(
                        position = flower.position,
                        velocity = Offset(Random.nextFloat() * 40f - 20f, -Random.nextFloat() * 50f - 10f),
                        color = flower.flowerColor.color,
                        size = 5f
                    )
                )
            }
        }

        // Calculate total harvest power
        val baseToolPollen = tool.pollenPerGather.toFloat()
        var multiplier = curField.pollenMultiplier

        // Swarm bonuses
        var swarmGatherBonus = 0f
        var critChance = 0.05f + tool.critChanceBonus

        for (bee in gameState.equippedBees) {
            swarmGatherBonus += bee.effectiveGatherPower * 0.3f
            if (bee.beeType.abilityType == AbilityType.CRITICAL_CHANCE) {
                critChance += bee.beeType.abilityValue
            }
            if (bee.beeType.abilityType == AbilityType.POLLEN_MULTIPLIER) {
                multiplier += bee.beeType.abilityValue
            }
        }

        val isCrit = Random.nextFloat() < critChance
        val critMult = if (isCrit) 2.5f else 1.0f

        val calculated = ((baseToolPollen + hits * 2f + swarmGatherBonus) * multiplier * critMult).toLong().coerceAtLeast(1L)
        val spaceLeft = gameState.maxPollenCapacity - gameState.pollen
        val actualPollen = min(calculated, spaceLeft)

        gameState.pollen += actualPollen
        gameState.totalPollenCollected += actualPollen
        trackQuestProgress(QuestObjectiveType.COLLECT_POLLEN, actualPollen)

        // Floating text
        val textStr = if (isCrit) "+$actualPollen CRIT!" else "+$actualPollen"
        val textColor = if (isCrit) Color(0xFFFF1744) else Color(0xFFFFEE58)
        addFloatingText(textStr, playerPos + Offset(0f, -30f), textColor, isCrit)

        // Tutorial Step 2 Check
        if (gameState.tutorialStep == 2 && gameState.pollen >= 30) {
            gameState.tutorialStep = 3
        }

        return true
    }

    private fun gatherFromNearbyFlower(beeEntity: SwarmBeeEntity) {
        if (gameState.pollen >= gameState.maxPollenCapacity) return
        var nearestFlower: FlowerState? = null
        var nearestDist = 120f
        for (f in flowers) {
            val dist = (f.position - beeEntity.position).getDistance()
            if (dist < nearestDist && f.pollenRemaining > 10f) {
                nearestDist = dist
                nearestFlower = f
            }
        }

        if (nearestFlower != null) {
            nearestFlower.pollenRemaining -= 10f
            val basePower = beeEntity.instance.effectiveGatherPower
            val curField = _currentField.value
            val fieldMult = curField?.pollenMultiplier ?: 1.0f
            val gain = min(gameState.maxPollenCapacity - gameState.pollen, (basePower * fieldMult).toLong().coerceAtLeast(1L))
            if (gain > 0) {
                gameState.pollen += gain
                gameState.totalPollenCollected += gain
                trackQuestProgress(QuestObjectiveType.COLLECT_POLLEN, gain)

                particles.add(
                    Particle(
                        position = nearestFlower.position,
                        velocity = Offset(0f, -30f),
                        color = nearestFlower.flowerColor.color,
                        size = 4f
                    )
                )
            }
        }
    }

    fun startConvertingHoney() {
        if (gameState.pollen > 0) {
            _isConvertingHoney.value = true
        }
    }

    fun stopConvertingHoney() {
        _isConvertingHoney.value = false
    }

    fun toggleConvertingHoney() {
        if (_isConvertingHoney.value) {
            stopConvertingHoney()
        } else {
            startConvertingHoney()
        }
    }

    fun buyNextHiveSlot(): Boolean {
        if (!gameState.canAffordNextSlot()) return false
        val cost = gameState.nextSlotCost
        gameState.honey -= cost
        gameState.hiveSlots++
        GameAudio.playSlotPurchase()
        trackQuestProgress(QuestObjectiveType.BUY_SLOT, gameState.hiveSlots.toLong())

        // Tutorial Step 4 Check
        if (gameState.tutorialStep == 6) {
            gameState.tutorialStep = 7
        }

        saveManager.saveGame(gameState)
        return true
    }

    fun addFloatingText(text: String, pos: Offset, color: Color, isCrit: Boolean = false) {
        floatingTexts.add(
            FloatingText(
                text = text,
                position = pos + Offset(Random.nextFloat() * 20f - 10f, Random.nextFloat() * 10f - 5f),
                color = color,
                isCrit = isCrit
            )
        )
    }

    fun trackQuestProgress(type: QuestObjectiveType, amount: Long) {
        for (q in gameState.quests) {
            if (!q.isClaimed && q.objectiveType == type) {
                if (type == QuestObjectiveType.BUY_SLOT || type == QuestObjectiveType.OWN_BEES_COUNT || type == QuestObjectiveType.LEVEL_UP_BEE) {
                    q.currentProgress = max(q.currentProgress, amount)
                } else {
                    q.currentProgress += amount
                }
            }
        }
    }

    fun isInField(field: Field): Boolean {
        return field.bounds.contains(playerPos)
    }
}
