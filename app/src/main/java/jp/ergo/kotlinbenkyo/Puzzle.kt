package jp.ergo.kotlinbenkyo


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
            Config.default.rightEdge -> null
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
            Config.default.bottomEdge -> null
            else -> Address(x, y + 1)
        }
    }

    fun up(): Address? {
        return when (y) {
            0 -> null
            else -> Address(x, y - 1)
        }
    }

    fun origin(): Int {
        return y * Config.default.columnCount + x
    }

    override fun toString(): String {
        return "Address(x=$x, y=$y, origin=${origin()})"
    }

    companion object {
        fun of(origin: Int): Address {
            val x = origin % Config.default.columnCount
            val y = (origin / Config.default.columnCount)
            return Address(x, y)

        }
    }

}

class Config(val size: Int) {
    companion object {
        var default = Config(25)
    }

    /**
     * 下端のyのindex。5x5なら4
     * 0 origin
     */
    val bottomEdge: Int by lazy { Math.sqrt(size.toDouble()).toInt() - 1 }
    /**
     * 右端のxのindex。5x5なら4
     * 0 origin
     */
    val rightEdge: Int by lazy { Math.sqrt(size.toDouble()).toInt() - 1 }

    /**
     * 列の数。5x5なら5
     * 1 origin
     */
    val columnCount: Int by lazy { Math.sqrt(size.toDouble()).toInt() }
}


class Field internal constructor(val masus: Map<Address, Direction?>) {

    companion object {
        fun createField(rawDirections: List<Int>): Field? {
            validate(rawDirections.size)
            Config.default = Config(rawDirections.size)

            return Field(
                    IntRange(0, Config.default.bottomEdge)
                            .map { x -> IntRange(0, Config.default.rightEdge).map { y -> Address(x, y) } }
                            .flatten()
                            .sortedWith(Comparator { left, right -> left.origin() - right.origin() })
                            .zip(rawDirections.map { Direction.of(it) }, ::Pair).toMap()
            )
        }

        fun empty(): Field {
            return Field(IntRange(0, Config.default.bottomEdge)
                    .map { x -> IntRange(0, Config.default.rightEdge).map { y -> Address(x, y) } }
                    .flatten()
                    .zip(IntRange(0, Config.default.size - 1).map { null as Direction? }, ::Pair)
                    .toMap())
        }

        fun validate(size: Int) {
            val sqrt = Math.sqrt(size.toDouble()).toInt()
            if (size / sqrt != sqrt) throw IllegalArgumentException("リストのサイズは5x5など整数の平方根を取れなければなりません。size: " + size)
        }
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
        return masus.mapValues { if (address.contains(it.key)) null else it.value }.let {
            Logger.d("remove from ${address.first()}")
            Logger.d(Field(it).toArrowSquare())
            Field(it)
        }
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
            else -> moveField(
                    filtered.map { slide(it.key, it.value) }.flatten().toMap()  // 変更があった要素のMap
                            .let { masus.filterNot { e -> it.contains(e.key) } + it }   // 変更がなかったものとマージ
                    , canMove
                    , slide)
        }
    }

    /**
     * @return Directionがnullである要素を下詰にしたFieldを返す
     */
    fun moveDownField(): Field {
        return moveField(masus, canDown, slideDown).let {
            Logger.d("move down")
            Logger.d(it.toArrowSquare())
            it
        }
    }


    private val slideDown: (Address, Direction?) -> List<Pair<Address, Direction?>> = { address: Address, direction: Direction? ->
        listOf(address to null, address.down()!! to direction)
    }

    private val canDown: (Map<Address, Direction?>, Address) -> Boolean = { masus: Map<Address, Direction?>, address: Address ->
        masus[address] != null && masus[address.down()] == null && !isBottomEdge(address)
    }

    /**
     * @return Directionがnullである要素を左詰めにしたFieldを返す。
     */
    fun moveLeftField(): Field {
        return moveField(masus, canLeft, slideLeft).let {
            Logger.d("move left")
            Logger.d(it.toArrowSquare())
            it
        }
    }

    private val slideLeft: (Address, Direction?) -> List<Pair<Address, Direction?>> = { address: Address, direction: Direction? ->
        listOf(address to null, address.left()!! to direction)
    }

    private val canLeft: (Map<Address, Direction?>, Address) -> Boolean = { masus: Map<Address, Direction?>, address: Address ->
        // 対象のAddressのDirectionがnullではない
        // 左端ではない
        // 対象のAddressの左側の一列が全部nullである
        masus[address] != null
                && !isLeftEdge(address)
                && isLeftRowEmpty(masus, address)
    }

    private fun isLeftRowEmpty(masus: Map<Address, Direction?>, address: Address): Boolean {
        return address.left()?.let {
            masus.filter { e -> e.key.x == it.x && e.value != null }.isEmpty()
        } ?: false
    }


    fun isBottomEdge(address: Address): Boolean {
        return address.y == Config.default.bottomEdge
    }

    fun isLeftEdge(address: Address): Boolean {
        return address.x == 0
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
        val address = IntRange(0, Config.default.bottomEdge).map { x -> IntRange(0, Config.default.rightEdge).map { y -> Address(x, y) } }.flatten()
        val directions = IntRange(0, Config.default.size - 1).map { null }
        val nullMasus: Map<Address, Direction?> = address.zip(directions, ::Pair).toMap()
        val sorted = (nullMasus + masus).toList().sortedWith(Comparator { left, right -> left.first.origin() - right.first.origin() })
        return sorted.fold("", { acc, pair ->
            val arrow = if (pair.second != null) pair.second!!.toArrow() else "-"
            when (pair.first.x) {
                Config.default.rightEdge -> acc + arrow + "\n"
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
