package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.game.GameEngine
import com.example.model.Field
import com.example.model.NPC
import kotlin.math.*

@Composable
fun WorldCanvas(
    engine: GameEngine,
    tick: Long,
    onNpcClick: (NPC) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalDensity.current

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapScreenOffset ->
                    // Convert screen tap offset to world offset
                    // Handled in parent or click detections
                }
            }
    ) {
        val screenWidth = size.width
        val screenHeight = size.height

        // Camera offset centered on player
        val cameraX = (screenWidth / 2f) - engine.playerPos.x
        val cameraY = (screenHeight / 2f) - engine.playerPos.y

        withTransform({
            translate(cameraX, cameraY)
        }) {
            // 1. Draw World Background (Grass & Paths)
            drawRect(
                color = Color(0xFF43732C),
                topLeft = Offset(0f, 0f),
                size = Size(2400f, 1600f)
            )

            // Cobblestone Dirt Path connecting Hive to Fields
            val pathColor = Color(0xFF8D6E63)
            drawRoundRect(
                color = pathColor,
                topLeft = Offset(500f, 820f),
                size = Size(1000f, 60f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            drawRoundRect(
                color = pathColor,
                topLeft = Offset(1150f, 400f),
                size = Size(60f, 500f),
                cornerRadius = CornerRadius(20f, 20f)
            )

            // 2. Draw Fields
            for (field in Field.ALL_FIELDS) {
                val b = field.bounds
                // Field Ground
                drawRoundRect(
                    color = field.groundColor,
                    topLeft = Offset(b.left, b.top),
                    size = Size(b.width, b.height),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                // Field Border
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.25f),
                    topLeft = Offset(b.left, b.top),
                    size = Size(b.width, b.height),
                    cornerRadius = CornerRadius(24f, 24f),
                    style = Stroke(width = 4f)
                )
            }

            // 3. Draw Flowers
            for (flower in engine.flowers) {
                val fx = flower.position.x
                val fy = flower.position.y
                val ratio = flower.pollenRemaining / flower.maxPollen
                val flowerSize = 10f + ratio * 6f

                // Petals
                drawCircle(
                    color = flower.flowerColor.color,
                    radius = flowerSize,
                    center = Offset(fx, fy)
                )
                // Center Pollen Core
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = flowerSize * 0.45f,
                    center = Offset(fx, fy)
                )
            }

            // 4. Draw Hive Structure & Honey Converter
            val hive = engine.hiveCenter
            // Hive Platform
            drawCircle(
                color = Color(0xFFFFA000),
                radius = 110f,
                center = hive
            )
            drawCircle(
                color = Color(0xFFFFB300),
                radius = 95f,
                center = hive
            )
            // Honeycomb cells inside Hive
            for (i in -2..2) {
                for (j in -2..2) {
                    if (i * i + j * j <= 4) {
                        drawCircle(
                            color = Color(0xFFFFF8E1).copy(alpha = 0.5f),
                            radius = 14f,
                            center = hive + Offset(i * 26f + (j % 2) * 13f, j * 24f)
                        )
                    }
                }
            }

            // Honey Converter Vat
            val vatPos = hive + Offset(-80f, 0f)
            drawRoundRect(
                color = Color(0xFF6D4C41),
                topLeft = Offset(vatPos.x - 22f, vatPos.y - 30f),
                size = Size(44f, 60f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(vatPos.x - 18f, vatPos.y - 10f),
                size = Size(36f, 35f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // 5. Draw NPCs
            for (npc in NPC.ALL_NPCS) {
                val pos = npc.position
                // NPC Base Ring
                drawCircle(
                    color = Color(0xFFFFA000).copy(alpha = 0.4f),
                    radius = 35f,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFF3E2723),
                    radius = 26f,
                    center = pos
                )
                // NPC Head Accent
                drawCircle(
                    color = Color(0xFF8D6E63),
                    radius = 18f,
                    center = pos
                )
            }

            // 6. Draw Player Character
            val pPos = engine.playerPos
            val facing = if (engine.playerFacingLeft) -1f else 1f

            // Player Shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(pPos.x - 20f, pPos.y + 16f),
                size = Size(40f, 16f)
            )

            // Backpack
            drawRoundRect(
                color = Color(0xFF8D6E63),
                topLeft = Offset(pPos.x - 18f * facing - 10f, pPos.y - 12f),
                size = Size(20f, 26f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Pollen level in backpack
            val pollenFrac = (engine.gameState.pollen.toFloat() / engine.gameState.maxPollenCapacity.coerceAtLeast(1L)).coerceIn(0f, 1f)
            if (pollenFrac > 0) {
                val fillH = 22f * pollenFrac
                drawRoundRect(
                    color = Color(0xFFFFEE58),
                    topLeft = Offset(pPos.x - 18f * facing - 8f, pPos.y + 10f - fillH),
                    size = Size(16f, fillH),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            // Player Body (Beekeeper Suit)
            drawRoundRect(
                color = Color(0xFFFFF9C4),
                topLeft = Offset(pPos.x - 16f, pPos.y - 16f),
                size = Size(32f, 34f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Beekeeper Hat & Veil
            drawCircle(
                color = Color(0xFFFFF59D),
                radius = 18f,
                center = Offset(pPos.x, pPos.y - 24f)
            )
            drawCircle(
                color = Color(0xFFFFA000),
                radius = 24f,
                center = Offset(pPos.x, pPos.y - 30f)
            )
            // Eyes
            drawCircle(
                color = Color.Black,
                radius = 3f,
                center = Offset(pPos.x + 6f * facing, pPos.y - 24f)
            )

            // Tool / Scoop Swinging
            val scoopBase = pPos + Offset(14f * facing, 0f)
            val swingRad = Math.toRadians((engine.scoopSwingAngle * facing).toDouble()).toFloat()
            val scoopArmEnd = scoopBase + Offset(cos(swingRad) * 28f * facing, sin(swingRad) * 28f)

            drawLine(
                color = Color(0xFF5D4037),
                start = scoopBase,
                end = scoopArmEnd,
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = Color(0xFFFFB300),
                radius = 8f,
                center = scoopArmEnd
            )

            // 7. Draw Swarm Bees (Followers)
            for (beeEntity in engine.swarmEntities) {
                val bPos = beeEntity.position
                val beeType = beeEntity.instance.beeType

                // Flapping Wings
                val wingSpread = sin(beeEntity.wingPhase) * 8f
                drawOval(
                    color = beeType.wingColor.copy(alpha = 0.85f),
                    topLeft = Offset(bPos.x - 14f, bPos.y - 14f - wingSpread),
                    size = Size(12f, 10f + abs(wingSpread))
                )
                drawOval(
                    color = beeType.wingColor.copy(alpha = 0.85f),
                    topLeft = Offset(bPos.x + 2f, bPos.y - 14f - wingSpread),
                    size = Size(12f, 10f + abs(wingSpread))
                )

                // Bee Body
                drawRoundRect(
                    color = beeType.primaryColor,
                    topLeft = Offset(bPos.x - 12f, bPos.y - 8f),
                    size = Size(24f, 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Bee Stripes
                drawRect(
                    color = beeType.stripeColor,
                    topLeft = Offset(bPos.x - 4f, bPos.y - 8f),
                    size = Size(4f, 16f)
                )
                drawRect(
                    color = beeType.stripeColor,
                    topLeft = Offset(bPos.x + 4f, bPos.y - 8f),
                    size = Size(4f, 16f)
                )

                // Bee Stinger
                val stingerPath = Path().apply {
                    moveTo(bPos.x - 12f, bPos.y)
                    lineTo(bPos.x - 18f, bPos.y - 2f)
                    lineTo(bPos.x - 18f, bPos.y + 2f)
                    close()
                }
                drawPath(stingerPath, beeType.stripeColor)

                // Bee Eye
                drawCircle(
                    color = beeType.eyeColor,
                    radius = 2.5f,
                    center = Offset(bPos.x + 8f, bPos.y - 2f)
                )

                // Rarity Aura for High Tiers
                if (beeType.rarity.isHighTier()) {
                    drawCircle(
                        color = beeType.rarity.glowColor.copy(alpha = 0.4f),
                        radius = 22f,
                        center = bPos,
                        style = Stroke(width = 3f)
                    )
                }
            }

            // 8. Draw Particles
            for (p in engine.particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size,
                    center = p.position
                )
            }
        }
    }
}
