// SPDX-License-Identifier: GPL-3.0-or-later

package com.ichi2.anki.reviewer

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.ichi2.anki.R
import com.ichi2.anki.settings.enums.NightTheme
import com.ichi2.themes.Themes
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Fork addition: the fonts of the Pixel Cold War night theme (`theme_pixel_cold_war.xml`).
 *
 * The theme's colours and shapes come from XML; the fonts are applied here so they reach only
 * the app's own chrome (buttons, counts), never user text such as deck names or card content.
 * Neo둥근모 (NeoDunggeunmo) for buttons and numbers, 마비옛체 (Mabinogi Classic) for small labels.
 */
object ForkPixelTheme {
    val isActive: Boolean
        get() = Themes.currentTheme == NightTheme.PIXEL_COLD_WAR

    fun neo(context: Context): Typeface = font(context, R.font.neodgm)

    fun mabinogi(context: Context): Typeface = font(context, R.font.mabinogi_classic)

    private fun font(
        context: Context,
        id: Int,
    ): Typeface = ResourcesCompat.getFont(context, id) ?: Typeface.MONOSPACE

    /**
     * Neo둥근모 text size in pixels for about [dp]: a multiple of 16px, because the font is drawn
     * on a 16px grid and blurs at other sizes. Same as `Px.neoPx` in the pixel-icons-cold-war repo.
     */
    fun neoPx(
        context: Context,
        dp: Float,
    ): Float {
        val density = context.resources.displayMetrics.density
        return 16f * max(1, (dp * density / 16f).roundToInt())
    }

    /** Neo둥근모 at [dp] (rounded to the font's grid) on [views] */
    fun applyNeo(
        dp: Float,
        vararg views: TextView?,
    ) {
        for (view in views) {
            view ?: continue
            view.typeface = neo(view.context)
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, neoPx(view.context, dp))
        }
    }
}
