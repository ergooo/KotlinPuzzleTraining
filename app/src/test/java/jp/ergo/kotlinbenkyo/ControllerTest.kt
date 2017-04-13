package jp.ergo.kotlinbenkyo

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class ControllerTest {

    @Test
    fun convertInputは文字列を数値のリストに変換する() {
        val testData = "0000120011333113332133222"
        val actual = Controller.convertInput(testData)
        val expect = listOf(0, 0, 0, 0, 1, 2, 0, 0, 1, 1, 3, 3, 3, 1, 1, 3, 3, 3, 2, 1, 3, 3, 2, 2, 2)
        MatcherAssert.assertThat(actual, CoreMatchers.`is`(expect))
    }
}