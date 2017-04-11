package jp.ergo.kotlinbenkyo

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class FieldFactoryTest {
    @Test
    fun create5x5Field() {
        val testData = (0..24).map { 1 }
        println("testData.size: " + testData.size)
        val actual = Field.FieldFactory.create5x5Field(testData)
        println(actual)
        actual?.let { it.masus.forEach { println(it) } }

        actual?.let {
            val list = it.trace(Address(0, 0))
            println("lis: " + list)
        }

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


}
