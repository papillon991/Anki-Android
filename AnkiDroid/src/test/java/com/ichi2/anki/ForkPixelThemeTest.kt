// SPDX-License-Identifier: GPL-3.0-or-later

package com.ichi2.anki

import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ichi2.anki.reviewer.ForkPixelTheme
import com.ichi2.anki.settings.PrefsRepository
import com.ichi2.anki.settings.enums.AppTheme
import com.ichi2.anki.settings.enums.NightTheme
import com.ichi2.themes.Themes
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

/** Fork: the Pixel Cold War night theme */
@RunWith(AndroidJUnit4::class)
class ForkPixelThemeTest : RobolectricTest() {
    override fun getCollectionStorageMode() = CollectionStorageMode.IN_MEMORY_WITH_MEDIA

    @Test
    fun `selected as the night theme it is a night theme with pixel fonts on the study screen`() =
        withNightTheme(NightTheme.PIXEL_COLD_WAR) { reviewer ->
            assertThat(Themes.currentTheme, equalTo(NightTheme.PIXEL_COLD_WAR))
            assertThat("cards get night mode", Themes.isNightTheme, equalTo(true))
            assertThat(ForkPixelTheme.isActive, equalTo(true))

            val neo = ForkPixelTheme.neo(reviewer)
            assertThat("Show answer", reviewer.flipText.typeface, equalTo(neo))
            assertThat("Good", reviewer.findViewById<TextView>(R.id.ease3).typeface, equalTo(neo))
            assertThat("new count", reviewer.findViewById<TextView>(R.id.new_number).typeface, equalTo(neo))
        }

    @Test
    fun `other night themes keep the system font`() =
        withNightTheme(NightTheme.BLACK) { reviewer ->
            assertThat(ForkPixelTheme.isActive, equalTo(false))
            assertThat(reviewer.flipText.typeface, not(equalTo(ForkPixelTheme.neo(reviewer))))
        }

    @Test
    fun `Neo size snaps to the font's 16px grid`() {
        val density = targetContext.resources.displayMetrics.density
        for (dp in listOf(12f, 16f, 24f, 32f)) {
            val px = ForkPixelTheme.neoPx(targetContext, dp)
            assertThat("$dp dp at density $density", px % 16f, equalTo(0f))
        }
    }

    private fun withNightTheme(
        theme: NightTheme,
        block: (Reviewer) -> Unit,
    ) {
        PrefsRepository(targetContext).apply {
            appTheme = AppTheme.NIGHT
            nightTheme = theme
        }
        addBasicNote("Hello", "World")
        val reviewer = ReviewerTest.startReviewer(this)
        advanceRobolectricLooper()
        block(reviewer)
    }

    private val Reviewer.flipText: TextView
        get() = findViewById<FrameLayout>(R.id.flashcard_layout_flip).getChildAt(0) as TextView
}
