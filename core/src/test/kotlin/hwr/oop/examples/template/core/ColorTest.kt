package hwr.oop.examples.template.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ColorTest {

    @Test
    fun `should contain exactly white and black`() {
        val values = Color.values()

        assertEquals(2, values.size)
        assertEquals(Color.WHITE, values[0])
        assertEquals(Color.BLACK, values[1])
    }

    @Test
    fun `should resolve enum by name`() {
        assertEquals(Color.WHITE, Color.valueOf("WHITE"))
        assertEquals(Color.BLACK, Color.valueOf("BLACK"))
    }
}