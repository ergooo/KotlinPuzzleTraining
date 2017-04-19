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
        val expect = listOf(Address(1, 0))
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは0000120011333113332133222のFieldを解く() {
        val testInput = "0000120011333113332133222"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(0, 20)
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは0003110131023201033102312のFieldを解く() {
        val testInput = "0003110131023201033102312"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(4, 6, 15, 16, 20, 16)
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは0030122111300110030103031のFieldを解く() {
        val testInput = "0030122111300110030103031"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(0, 3, 15, 16, 20)
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは2120132302300131130120002のFieldを解く() {
        val testInput = "2120132302300131130120002"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(3, 10, 12, 16, 21)
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは2121222113211132211332203のFieldを解く() {
        val testInput = "2121222113211132211332203"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(1,3, 7, 16, 21 )
        assertThat(actual, `is`(expect))
    }

    @Test
    fun getPahtは0000131122321021223200003のFieldを解く() {
        val testInput = "0000131122321021223200003"
        val testData = Field.createField(Controller.convertInput(testInput))!!
        val actual = Controller.getPath(testData).map { it.origin() }
        val expect = listOf(6)
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
