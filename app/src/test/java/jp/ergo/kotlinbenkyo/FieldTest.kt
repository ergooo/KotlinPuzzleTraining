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
        val actual = Field.create5x5Field(testData)
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
    fun removeFieldは指定したAddressのDirectionをnullに変える() {
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
                Address(1, 0),
                Address(2, 0),
                Address(4, 0)
        )
        assertThat(actual, `is`(expect))
    }

    @Test
    fun toArrowSquareTest() {
        val testData = "0000120011333113332133222"
        val sut = Field.create5x5Field(Controller.convertInput(testData))
        val actual = sut!!.collapseFrom(Address(0, 0)).toArrowSquare().trim()
        val expect = """
↑ ↑ ↑ - -
→ → ↑ ↑ ↑
→ → ↑ ↑ ←
→ ↓ ↓ ← ←
↓ ↓ ↓ ↓ ←
""".trim()
        assertThat(actual, `is`(expect))
    }

    @Test
    fun isEmptyはDirectionが全部nullの場合にtrueを返す() {
        val testData = mapOf(
                Address(0, 0) to null,
                Address(0, 1) to null,
                Address(0, 2) to null,
                Address(0, 3) to null,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.isEmpty()
        assertThat(actual, `is`(true))
    }

    @Test
    fun isEmptyはDirectionが全部nullでない場合にfalseを返す() {
        val testData = mapOf(
                Address(0, 0) to Direction.DOWN,
                Address(1, 0) to Direction.DOWN,
                Address(2, 0) to Direction.DOWN,
                Address(3, 0) to Direction.DOWN,
                Address(0, 4) to null
        )
        val sut = Field(testData)
        val actual = sut.isEmpty()
        assertThat(actual, `is`(false))
    }

    @Test
    fun EMPTYはすべてのDirectionがnullである() {
        val sut = Field.EMPTY
        sut.masus.values.forEach { assertThat(it, `is`(nullValue())) }
    }

    @Test
    fun EMPTYはすべてのDirectionがnullな別インスタンスとイコールで比較した時tureを返す() {
        val sut = Field.EMPTY

        val expect = Field((0..4).map { x -> (0..4).map { y -> Address(x, y) } }.flatten().zip((0..24).map { null as Direction? }, ::Pair).toMap())
        assertThat(sut, `is`(equalTo(expect)))

    }
}

class AddressTest {
    @Test
    fun originは位置をIntで返す() {
        assertThat(Address(0, 0).origin(), `is`(0))
        assertThat(Address(1, 0).origin(), `is`(1))
        assertThat(Address(2, 0).origin(), `is`(2))
        assertThat(Address(3, 0).origin(), `is`(3))
        assertThat(Address(4, 0).origin(), `is`(4))
        assertThat(Address(0, 1).origin(), `is`(5))
        assertThat(Address(1, 1).origin(), `is`(6))
        assertThat(Address(2, 1).origin(), `is`(7))
        assertThat(Address(3, 1).origin(), `is`(8))
        assertThat(Address(4, 1).origin(), `is`(9))
        assertThat(Address(0, 2).origin(), `is`(10))
        assertThat(Address(1, 2).origin(), `is`(11))
        assertThat(Address(2, 2).origin(), `is`(12))
        assertThat(Address(3, 2).origin(), `is`(13))
        assertThat(Address(4, 2).origin(), `is`(14))
        assertThat(Address(0, 3).origin(), `is`(15))
        assertThat(Address(1, 3).origin(), `is`(16))
        assertThat(Address(2, 3).origin(), `is`(17))
        assertThat(Address(3, 3).origin(), `is`(18))
        assertThat(Address(4, 3).origin(), `is`(19))
        assertThat(Address(0, 4).origin(), `is`(20))
        assertThat(Address(1, 4).origin(), `is`(21))
        assertThat(Address(2, 4).origin(), `is`(22))
        assertThat(Address(3, 4).origin(), `is`(23))
        assertThat(Address(4, 4).origin(), `is`(24))
    }
}
