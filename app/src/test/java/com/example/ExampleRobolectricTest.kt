package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.GameState
import com.example.game.RngConfig
import com.example.model.BeeInstance
import com.example.model.BeeRarity
import com.example.model.BeeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bee Swarm RNG", appName)
  }

  @Test
  fun `verify bee slot doubling cost logic`() {
    val state = GameState(hiveSlots = 1)
    assertEquals(1000L, state.nextSlotCost)

    state.hiveSlots = 2
    assertEquals(2000L, state.nextSlotCost)

    state.hiveSlots = 3
    assertEquals(4000L, state.nextSlotCost)

    state.hiveSlots = 4
    assertEquals(8000L, state.nextSlotCost)

    state.hiveSlots = 5
    assertEquals(16000L, state.nextSlotCost)
  }

  @Test
  fun `verify bond leveling progression`() {
    val bee = BeeInstance(beeType = BeeType.BASIC_BEE, level = 1, currentBond = 0)
    // 1 Treat = 10 Bond. Level 1->2 requires 10 Bond.
    val levelsGained = bee.feedTreats(1)
    assertEquals(1, levelsGained)
    assertEquals(2, bee.level)
    assertEquals(0, bee.currentBond)

    // Level 2->3 requires 20 Bond (2 Treats)
    val lvGained2 = bee.feedTreats(2)
    assertEquals(1, lvGained2)
    assertEquals(3, bee.level)

    // Feed 1 Treat (10 Bond). Level 3->4 requires 30 Bond.
    val lvGained3 = bee.feedTreats(1)
    assertEquals(0, lvGained3)
    assertEquals(3, bee.level)
    assertEquals(10, bee.currentBond)
  }

  @Test
  fun `verify all 20 bee types distribution across 9 rarities`() {
    val allBees = BeeType.entries
    assertEquals(20, allBees.size)

    assertEquals(1, allBees.count { it.rarity == BeeRarity.COMMON })
    assertEquals(4, allBees.count { it.rarity == BeeRarity.UNCOMMON })
    assertEquals(5, allBees.count { it.rarity == BeeRarity.RARE })
    assertEquals(2, allBees.count { it.rarity == BeeRarity.EPIC })
    assertEquals(1, allBees.count { it.rarity == BeeRarity.LEGENDARY })
    assertEquals(3, allBees.count { it.rarity == BeeRarity.MYTHIC })
    assertEquals(2, allBees.count { it.rarity == BeeRarity.DIVINE })
    assertEquals(1, allBees.count { it.rarity == BeeRarity.CELESTIAL })
    assertEquals(1, allBees.count { it.rarity == BeeRarity.SECRET })
  }
}
