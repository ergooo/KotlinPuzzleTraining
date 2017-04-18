package jp.ergo.kotlinbenkyo

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ControllerTest {
    @Before
    fun setUp(){
        Config.default = Config(25)
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
        val testData = Field.create5x5Field(Controller.convertInput(testInput))!!
        val actual = Controller.toCollapsedMap(testData)
        val testDataSquare = """
→ ← ↑ ↑ ↑
→ → ↑ ↑ ↑
→ → ↑ ↑ ←
→ ↓ ↓ ← ←
↓ ↓ ↓ ↓ ←
        """.trim()
        assertThat(testData.toArrowSquare().trim(), `is`(testDataSquare))

        val actual00 = """
↑ ↑ ↑ - -
→ → ↑ ↑ ↑
→ → ↑ ↑ ←
→ ↓ ↓ ← ←
↓ ↓ ↓ ↓ ←
        """.trim()
        assertThat(actual.filter { it.value == Address(1,0) }.keys.first().toArrowSquare().trim(), `is`(actual00))

        val actual01 = """
↑ ↑ - - -
→ ← ↑ ↑ -
→ → ↑ ↑ ←
→ ↓ ↓ ← ←
↓ ↓ ↓ ↓ ←
""".trim()
        assertThat(actual.filter { it.value == Address(0,1) }.keys.first().toArrowSquare().trim(), `is`(actual01))
    }
}
