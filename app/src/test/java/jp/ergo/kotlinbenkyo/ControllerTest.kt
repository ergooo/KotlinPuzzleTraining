package jp.ergo.kotlinbenkyo

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ControllerTest {

    @Test
    fun getPathは3x3のFieldを解く() {
        val testInput = "1200"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData)
        val expect = listOf(Address(1,0))
        assertThat(actual, `is`(expect))        
    }

    @Test
    fun convertInputは文字列を数値のリストに変換する() {
        val testData = "0000120011333113332133222"
        val actual = Controller.convertInput(testData)
        val expect = listOf(0, 0, 0, 0, 1, 2, 0, 0, 1, 1, 3, 3, 3, 1, 1, 3, 3, 3, 2, 1, 3, 3, 2, 2, 2)
        MatcherAssert.assertThat(actual, CoreMatchers.`is`(expect))
    }

    @Test
    fun toCollapsedMap() {
        val testInput = "0000120011333113332133222"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.toCollapsedMap(testData)
        val testDataSquare = """
→ → → → ↓
← → → ↓ ↓
↑ ↑ ↑ ↓ ↓
↑ ↑ ↑ ← ↓
↑ ↑ ← ← ←
        """.trim()
        assertThat(testData.toArrowSquare().trim(), `is`(testDataSquare))

        val actual00 = """
→ - - - -
← - - - -
↑ - - - -
↑ - - - -
↑ - - - -
        """.trim()
        assertThat(actual.filter { it.value == Address(1, 0) }.keys.first().toArrowSquare().trim(), `is`(actual00))

        val actual01 = """
→ → → ↓ -
→ → → ↓ ↓
↑ ↑ ↑ ↓ ↓
↑ ↑ ↑ ← ↓
↑ ↑ ← ← ←
""".trim()
        assertThat(actual.filter { it.value == Address(0, 1) }.keys.first().toArrowSquare().trim(), `is`(actual01))
    }
}
