// SPDX-License-Identifier: GPL-3.0-or-later

package com.ichi2.anki

import android.view.View
import android.widget.LinearLayout
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ichi2.anki.common.preferences.sharedPrefs
import com.ichi2.utils.dp
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

/** Fork: the two-button answer bar (Again | Good | Again) of the legacy [Reviewer] */
@RunWith(AndroidJUnit4::class)
class ForkTwoButtonBarTest : RobolectricTest() {
    override fun getCollectionStorageMode() = CollectionStorageMode.IN_MEMORY_WITH_MEDIA

    @Test
    fun `bar is on by default - Again, Good, Again at double height`() =
        withAnswerShown { reviewer ->
            assertThat("Again", reviewer.shown(R.id.flashcard_layout_ease1), equalTo(true))
            assertThat("Hard is hidden", reviewer.shown(R.id.flashcard_layout_ease2), equalTo(false))
            assertThat("Good", reviewer.shown(R.id.flashcard_layout_ease3), equalTo(true))
            assertThat("Easy is hidden", reviewer.shown(R.id.flashcard_layout_ease4), equalTo(false))
            assertThat("right-hand Again", reviewer.shown(R.id.flashcard_layout_ease1_mirror), equalTo(true))

            assertThat("Good takes half the width", reviewer.weightOf(R.id.flashcard_layout_ease3), equalTo(2f))
            val doubled = 96.dp.toPx(targetContext)
            assertThat(reviewer.heightOf(R.id.flashcard_layout_ease1), equalTo(doubled))
            assertThat(reviewer.heightOf(R.id.flashcard_layout_ease1_mirror), equalTo(doubled))
            assertThat("'Show answer' matches", reviewer.heightOf(R.id.flashcard_layout_flip), equalTo(doubled))
        }

    @Test
    fun `right-hand Again answers Again`() =
        withAnswerShown { reviewer ->
            reviewer.findViewById<View>(R.id.flashcard_layout_ease1_mirror).performClick()
            advanceRobolectricLooper()
            assertThat(reviewer.isDisplayingAnswer, equalTo(false))
            assertThat("revlog ease", lastAnsweredEase(), equalTo(1L))
        }

    @Test
    fun `bar off - the four buttons as upstream`() =
        withAnswerShown(twoButtonBar = false) { reviewer ->
            for (id in listOf(
                R.id.flashcard_layout_ease1,
                R.id.flashcard_layout_ease2,
                R.id.flashcard_layout_ease3,
                R.id.flashcard_layout_ease4,
            )) {
                assertThat(reviewer.shown(id), equalTo(true))
            }
            assertThat(reviewer.shown(R.id.flashcard_layout_ease1_mirror), equalTo(false))
            assertThat(reviewer.weightOf(R.id.flashcard_layout_ease3), equalTo(1f))
            assertThat(reviewer.heightOf(R.id.flashcard_layout_ease1), equalTo(48.dp.toPx(targetContext)))
        }

    @Test
    fun `large answer buttons keep their 2x2 grid`() {
        targetContext.sharedPrefs().edit { putBoolean("showLargeAnswerButtons", true) }
        withAnswerShown { reviewer ->
            assertThat(reviewer.shown(R.id.flashcard_layout_ease2), equalTo(true))
            assertThat(reviewer.shown(R.id.flashcard_layout_ease4), equalTo(true))
            assertThat(reviewer.shown(R.id.flashcard_layout_ease1_mirror), equalTo(false))
        }
    }

    private fun withAnswerShown(
        twoButtonBar: Boolean? = null,
        block: (Reviewer) -> Unit,
    ) {
        twoButtonBar?.let { on ->
            targetContext.sharedPrefs().edit {
                putBoolean(targetContext.getString(R.string.fork_two_button_bar_key), on)
            }
        }
        addBasicNote("Hello", "World")
        val reviewer = ReviewerTest.startReviewer(this)
        advanceRobolectricLooper()
        reviewer.displayCardAnswer()
        advanceRobolectricLooper()
        block(reviewer)
    }

    private fun lastAnsweredEase(): Long = col.db.queryLongScalar("select ease from revlog order by id desc limit 1")

    /** Large answer buttons rebuild the bar from the four buttons, detaching the mirror */
    private fun Reviewer.shown(id: Int) = findViewById<View?>(id)?.visibility == View.VISIBLE

    private fun Reviewer.heightOf(id: Int) = findViewById<View>(id).layoutParams.height

    private fun Reviewer.weightOf(id: Int) = (findViewById<View>(id).layoutParams as LinearLayout.LayoutParams).weight
}
