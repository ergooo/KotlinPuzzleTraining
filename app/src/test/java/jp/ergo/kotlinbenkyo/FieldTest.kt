package jp.ergo.kotlinbenkyo

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.Test

class FieldTest {
    @Test
    fun collapseFromTest() {
        val testData = "0000120011333113332133222"
        val sut = Field.create5x5Field(Controller.convertInput(testData))
        val actual = sut!!.collapseFrom(Address(0, 0))

        assertThat(actual.masus[Address(3, 0)], IsNull())
        assertThat(actual.masus[Address(4, 0)], IsNull())
    }

    @Test
    fun create5x5FieldTest() {
        val testData = (0..24).map { 1 }
        assertThat(testData.size, `is`(25))
        val actual = Field.FieldFactory.create5x5Field(testData)
        val expect = Field(mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.DOWN,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to Direction.DOWN,
                Address(1, 0) to Direction.DOWN,
                Address(1, 1) to Direction.DOWN,
                Address(1, 2) to Direction.DOWN,
                Address(1, 3) to Direction.DOWN,
                Address(1, 4) to Direction.DOWN,
                Address(2, 0) to Direction.DOWN,
                Address(2, 1) to Direction.DOWN,
                Address(2, 2) to Direction.DOWN,
                Address(2, 3) to Direction.DOWN,
                Address(2, 4) to Direction.DOWN,
                Address(3, 0) to Direction.DOWN,
                Address(3, 1) to Direction.DOWN,
                Address(3, 2) to Direction.DOWN,
                Address(3, 3) to Direction.DOWN,
                Address(3, 4) to Direction.DOWN,
                Address(4, 0) to Direction.DOWN,
                Address(4, 1) to Direction.DOWN,
                Address(4, 2) to Direction.DOWN,
                Address(4, 3) to Direction.DOWN,
                Address(4, 4) to Direction.DOWN
        ))
        assertThat(actual, `is`(expect))

    }

    @Test
    fun removeFieldTest() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.DOWN,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.removedField(listOf(Address(0, 0), Address(0, 1)))
        val expect = Field(mapOf(
                Address(0, 0) to null,
                Address(0, 1) to null,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to null
        ))
        assertThat(actual, `is`(expect))
    }

    @Test
    fun traceTest() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.DOWN,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.trace(Address(0, 0))
        val expect = listOf(
                Address(0, 0),
                Address(0, 1),
                Address(0, 2),
                Address(0, 3))
        assertThat(actual, `is`(expect))
    }

    @Test
    fun testTraceは向き合ったDirection同士で無限ループにならない() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.UP
        )
        val sut = Field(testData)
        val actual = sut.trace(Address(0, 0))
        assertThat(actual, `is`(not(nullValue())))
    }

    @Test
    fun testTraceは一度辿ったアドレスはカウントせずそこで終わる() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.UP,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.trace(Address(0, 0))
        val expect = listOf(
                Address(0, 0),
                Address(0, 1))
        assertThat(actual, `is`(expect))
    }

    @Test
    fun moveDownTest() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(0, 1) to Direction.DOWN,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.moveDownField()

        val expect = Field(mapOf(
                Address(0, 0) to null,
                Address(0, 1) to Direction.DOWN,
                Address(0, 2) to Direction.DOWN,
                Address(0, 3) to Direction.DOWN,
                Address(0, 4) to Direction.DOWN)
        )
        assertThat(actual, `is`(expect))
    }

    @Test
    fun moveLeftTest() {
        val testData = mapOf(
                Address(0, 0) to null,
                Address(1, 0) to Direction.DOWN,
                Address(2, 0) to Direction.DOWN,
                Address(3, 0) to Direction.DOWN,
                Address(4, 0) to Direction.DOWN
        )
        val sut = Field(testData)
        val actual = sut.moveLeftField()

        val expect = Field(mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(1, 0) to Direction.DOWN,
                Address(2, 0) to Direction.DOWN,
                Address(3, 0) to Direction.DOWN,
                Address(4, 0) to null)
        )
        assertThat(actual, `is`(expect))
    }

    @Test
    fun availableAddressはDirectionがnullでないAddressのリストを返却する() {
        val testData = mapOf(
                Address(0, 0) to null,
                Address(1, 0) to Direction.DOWN,
                Address(2, 0) to Direction.DOWN,
                Address(3, 0) to null,
                Address(4, 0) to Direction.DOWN
        )
        val sut = Field(testData)
        val actual = sut.availableAddress()
        val expect = listOf(
                Address(1,0),
                Address(2,0),
                Address(4,0)
        )
        assertThat(actual, `is`(expect))
    }
}