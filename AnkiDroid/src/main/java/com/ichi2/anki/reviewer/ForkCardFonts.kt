// SPDX-License-Identifier: GPL-3.0-or-later

package com.ichi2.anki.reviewer

import android.content.SharedPreferences
import com.ichi2.anki.AnkiDroidApp

/**
 * Fork addition: lets the user force one of two font sets on the card body.
 * The `@font-face` files live in `assets/fonts/fork` and are served by
 * [com.ichi2.anki.ViewerResourceHandler] under [FONT_PATH_PREFIX].
 */
object ForkCardFonts {
    const val PREF_KEY = "forkCardFont"
    const val FONT_PATH_PREFIX = "/_fork_fonts/"
    const val DECK = "deck"
    const val MYEONGJO = "myeongjo"
    const val GOTHIC = "gothic"
    const val DEFAULT = GOTHIC

    private const val MYEONGJO_STACK = "'Lora', 'MaruBuri', 'Shippori Mincho', serif"
    private const val GOTHIC_STACK = "'IBM Plex Sans JP', 'IBM Plex Sans KR', 'IBM Plex Sans', sans-serif"

    private const val CJK_RANGE =
        "U+3000-303F, U+3040-30FF, U+31F0-31FF, U+3400-4DBF, U+4E00-9FFF, U+F900-FAFF, U+FF00-FFEF"

    private fun face(
        family: String,
        file: String,
        weight: String,
        style: String = "normal",
        range: String? = null,
    ): String =
        "@font-face{font-family:'$family';src:url('$FONT_PATH_PREFIX$file') format('woff2');" +
            "font-weight:$weight;font-style:$style;font-display:swap;" +
            (if (range != null) "unicode-range:$range;" else "") + "}"

    private val faces: String =
        listOf(
            face("Lora", "Lora.woff2", "400 700"),
            face("Lora", "Lora-Italic.woff2", "400 700", "italic"),
            face("MaruBuri", "MaruBuri-Regular.woff2", "400"),
            face("MaruBuri", "MaruBuri-Bold.woff2", "700"),
            face("IBM Plex Sans", "IBMPlexSans.woff2", "100 700"),
            face("IBM Plex Sans", "IBMPlexSans-Italic.woff2", "100 700", "italic"),
            face("IBM Plex Sans KR", "IBMPlexSansKR-Regular.woff2", "400"),
            face("IBM Plex Sans KR", "IBMPlexSansKR-Bold.woff2", "700"),
            face("Shippori Mincho", "ShipporiMincho-Regular.woff2", "400", range = CJK_RANGE),
            face("Shippori Mincho", "ShipporiMincho-Bold.woff2", "700", range = CJK_RANGE),
            face("IBM Plex Sans JP", "IBMPlexSansJP-Regular.woff2", "400", range = CJK_RANGE),
            face("IBM Plex Sans JP", "IBMPlexSansJP-Bold.woff2", "700", range = CJK_RANGE),
        ).joinToString("\n")

    /**
     * CSS forcing [choice] on the card body; empty for [DECK] (keep the note type's own fonts).
     * Monospace parts (code, the type-in-answer box) and MathJax keep their own fonts.
     */
    fun css(choice: String?): String {
        val stack =
            when (choice) {
                MYEONGJO -> MYEONGJO_STACK
                GOTHIC -> GOTHIC_STACK
                else -> return ""
            }
        return "$faces\n" +
            ".card, .card *:not(code, pre, kbd, samp, #typeans, mjx-container, mjx-container *)" +
            " { font-family: $stack !important; }\n"
    }

    /** CSS for the user's current choice. */
    fun css(prefs: SharedPreferences): String = css(prefs.getString(PREF_KEY, DEFAULT))

    fun currentCss(): String = css(AnkiDroidApp.sharedPrefs())
}
