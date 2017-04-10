package jp.ergo.kotlinbenkyo

import org.junit.Test

import org.junit.Assert.*

class FieldFactoryTest {
    @Test
    fun create5x5Field() {
        val testData = (0..24).map{1}
        println("testData.size: " + testData.size)
        val actual = Field.FieldFactory.create5x5Field(testData)
        println(actual)
        actual?.let { it.masus.forEach { println(it) } }

        actual?.let {
            val list = it.trace(Address(0,0))
            println("lis: " + list)
        }

    }

}
