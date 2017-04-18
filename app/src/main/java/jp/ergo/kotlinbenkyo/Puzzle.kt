package jp.ergo.kotlinbenkyo

import jp.ergo.kotlinbenkyo.Config.bottomEdge
import jp.ergo.kotlinbenkyo.Config.columnCount
import jp.ergo.kotlinbenkyo.Config.rightEdge
import jp.ergo.kotlinbenkyo.Config.size


enum class Direction(val rawValue: Int) {
    RIGHT(0),
    DOWN(1),
    LEFT(2),
    UP(3);

    companion object {
        fun of(rawValue: Int): Direction? {
            return Direction.values().filter { it.rawValue == rawValue }.firstOrNull()
        }
    }

    fun toArrow(): String {
        return when (this) {
            RIGHT -> "→"
            DOWN -> "↓"
            LEFT -> "←"
            UP -> "↑"
        }
    }
}


data class Address(val x: Int, val y: Int) {
    fun right(): Address? {
        return when (x) {
            rightEdge -> null
            else -> Address(x + 1, y)
        }
    }

    fun left(): Address? {
        return when (x) {
            0 -> null
            else -> Address(x - 1, y)
        }
    }

    fun down(): Address? {
        return when (y) {
            bottomEdge -> null
            else -> Address(x, y + 1)
        }
    }

    fun up(): Address? {
        return when (y) {
            0 -> null
            else -> Address(x, y - 1)
        }
    }

    fun isBottomEdge(): Boolean {
        return y == bottomEdge
    }

    fun isLeftEdge(): Boolean {
        return x == 0
    }

    fun origin(): Int {
        return y * columnCount + x
    }

}

object Config {
    private var s: Int = 25;

    fun init(size: Int) {
        this.s = size
    }

    /**
     * 下端のyのindex。5x5なら4
     * 0 origin
     */
    val bottomEdge: Int by lazy { Math.sqrt(s.toDouble()).toInt() - 1 }
    /**
     * 右端のxのindex。5x5なら4
     * 0 origin
     */
    val rightEdge: Int by lazy { Math.sqrt(s.toDouble()).toInt() - 1 }

    /**
     * 列の数。5x5なら5
     * 1 origin
     */
    val columnCount: Int by lazy { Math.sqrt(s.toDouble()).toInt() }

    val size: Int by lazy { s }

}


class Field internal constructor(val masus: Map<Address, Direction?>) {

    companion object {
        fun create5x5Field(rawDirections: List<Int>): Field? {
            if (rawDirections.size != size) return null
            val directions = rawDirections.map { Direction.of(it) }
            val addresses = IntRange(0, bottomEdge).map { x -> IntRange(0, rightEdge).map { y -> Address(x, y) } }.flatten()
            val masus = addresses.zip(directions, ::Pair).toMap()
            return Field(masus)
        }

        val EMPTY = Field(IntRange(0, bottomEdge).map { x -> IntRange(0, rightEdge).map { y -> Address(x, y) } }.flatten().zip(IntRange(0, size - 1).map { null as Direction? }, ::Pair).toMap())
    }

    /**
     * @return 指定したAddressの示す次のAddressを返す。次のAddressが範囲外であればnull
     */
    fun nextTo(address: Address): Address? {
        return when (masus[address]) {
            Direction.RIGHT -> address.right()
            Direction.DOWN -> address.down()
            Direction.LEFT -> address.left()
            Direction.UP -> address.up()
            else -> null
        }
    }

    /**
     * @return 指定したアドレスリストのDirectionをnullにしたFieldを返す。
     */
    fun removedField(address: List<Address>): Field {
        val removedMasus = masus.map { Pair(it.key, if (address.contains(it.key)) null else it.value) }.toMap()
        return Field(removedMasus)
    }

    /**
     * @return 自身のMasus内にcanMoveに適合する要素が含まれている場合、再帰的にslideに適応させた要素で置き換えたFieldを返す。
     */
    tailrec private fun moveField(masus: Map<Address, Direction?>,
                                  canMove: (Map<Address, Direction?>, Address) -> Boolean,
                                  slide: (Address, Direction?) -> List<Pair<Address, Direction?>>): Field {
        val filtered = masus.filter { canMove(masus, it.key) }
        return when {
            filtered.isEmpty() -> Field(masus)
            else -> {
                // 変更があった要素のMap
                val changedMap = filtered.map { slide(it.key, it.value) }.flatten().toMap()
                // 変更のなかった要素とマージ
                val merged = masus.filterNot { changedMap.contains(it.key) } + changedMap
                moveField(merged, canMove, slide)
            }
        }
    }

    /**
     * @return Directionがnullである要素を下詰にしたFieldを返す
     */
    fun moveDownField(): Field {
        return moveField(masus, canDown, slideDown)
    }


    private val slideDown: (Address, Direction?) -> List<Pair<Address, Direction?>> = { address: Address, direction: Direction? ->
        listOf(address to null, address.down()!! to direction)
    }

    private val canDown: (Map<Address, Direction?>, Address) -> Boolean = { masus: Map<Address, Direction?>, address: Address ->
        masus[address] != null && masus[address.down()] == null && !address.isBottomEdge()
    }

    /**
     * @return Directionがnullである要素を左詰めにしたFieldを返す。
     */
    fun moveLeftField(): Field {
        return moveField(masus, canLeft, slideLeft)
    }

    private val slideLeft: (Address, Direction?) -> List<Pair<Address, Direction?>> = { address: Address, direction: Direction? ->
        listOf(address to null, address.left()!! to direction)
    }

    private val canLeft: (Map<Address, Direction?>, Address) -> Boolean = { masus: Map<Address, Direction?>, address: Address ->
        masus[address] != null && masus[address.left()] == null && !address.isLeftEdge()
    }

    /**
     * @return 指定したAddressからDirectionを辿ったAddressのリストを返す。
     */
    fun trace(address: Address): List<Address> {
        return trace(listOf(address), address)
    }

    tailrec private fun trace(acc: List<Address>, address: Address): List<Address> {
        val nextAddress = nextTo(address)
        return when {
            nextAddress == null -> acc // 端っこに来たのでaccをそのまま返す
            masus[nextAddress] == null -> acc // 次Directionがないのでそこでおしまい
            acc.contains(nextAddress) -> acc // 既に辿ったところなのでそこで終了
            else -> trace(acc + nextAddress, nextAddress)
        }
    }

    fun collapseFrom(address: Address): Field {
        return removedField(trace(address)).moveDownField().moveLeftField()
    }

    fun availableAddress(): List<Address> {
        return masus.filter { it.value != null }.map { it.key }
    }

    /**
     * @return Directionが全てnullであればtrue
     */
    fun isEmpty(): Boolean {
        return masus.filter { it.value != null }.isEmpty()
    }

    /**
     * 目視確認用。こんな感じの文字列にする
     * → ← ↑ ↑ ↑
     * → → ↑ ↑ ↑
     * → → ↑ ↑ ←
     * → ↓ ↓ ← ←
     * ↓ ↓ ↓ ↓ ←
     */
    fun toArrowSquare(): String {
        val address = IntRange(0, bottomEdge).map { x -> IntRange(0, rightEdge).map { y -> Address(x, y) } }.flatten()
        val directions = IntRange(0, size - 1).map { null }
        val nullMasus: Map<Address, Direction?> = address.zip(directions, ::Pair).toMap()
        val sorted = (nullMasus + masus).toList().sortedWith(Comparator { left, right -> left.first.origin() - right.first.origin() })
        return sorted.fold("", { acc, pair ->
            val arrow = if (pair.second != null) pair.second!!.toArrow() else "-"
            when (pair.first.x) {
                rightEdge -> acc + arrow + "\n"
                else -> acc + arrow + " "
            }
        })
    }

    override fun toString(): String {
        return "Field(masus=$masus)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Field

        if (masus != other.masus) return false

        return true
    }

    override fun hashCode(): Int {
        return masus.hashCode()
    }
}
