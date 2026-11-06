// SPDX-License-Identifier: GPL-3.0-or-later

package com.ichi2.anki.reviewer

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ForkCardFontsTest {
    @Test
    fun `deck choice and unknown values add nothing`() {
        assertEquals("", ForkCardFonts.css(ForkCardFonts.DECK))
        assertEquals("", ForkCardFonts.css(null))
        assertEquals("", ForkCardFonts.css("nonsense"))
    }

    @Test
    fun `myeongjo set`() {
        val css = ForkCardFonts.css(ForkCardFonts.MYEONGJO)
        assertTrue("font-family: 'Lora', 'MaruBuri', 'Shippori Mincho', serif !important" in css)
        assertTrue("/_fork_fonts/MaruBuri-Regular.woff2" in css)
    }

    @Test
    fun `gothic set`() {
        val css = ForkCardFonts.css(ForkCardFonts.GOTHIC)
        assertTrue("font-family: 'IBM Plex Sans JP', 'IBM Plex Sans KR', 'IBM Plex Sans', sans-serif !important" in css)
        assertTrue("/_fork_fonts/IBMPlexSansJP-Regular.woff2" in css)
    }

    @Test
    fun `every font file referenced exists as an asset`() {
        val dir = java.io.File("src/main/assets/fonts/fork")
        val css = ForkCardFonts.css(ForkCardFonts.GOTHIC) + ForkCardFonts.css(ForkCardFonts.MYEONGJO)
        val names = Regex("/_fork_fonts/([^']+)").findAll(css).map { it.groupValues[1] }.toSet()
        assertEquals(12, names.size)
        names.forEach { assertTrue(java.io.File(dir, it).exists(), "missing $it") }
    }
}
