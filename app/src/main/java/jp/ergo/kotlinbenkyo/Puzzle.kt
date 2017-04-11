package jp.ergo.kotlinbenkyo


class Main {
    companion object {
        fun main(directions: List<Int>) {
        }
    }
}

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
}


data class Address(val x: Int, val y: Int) {
    fun right(): Address? {
        return when (x) {
            4 -> null
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
            4 -> null
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
        return y == 4
    }

    fun isLeftEdge(): Boolean {
        return x == 0
    }

}

class Field private constructor(val masus: Map<Address, Direction?>) {

    fun nextTo(address: Address): Address? {
        return when (masus[address]) {
            Direction.RIGHT -> address.right()
            Direction.DOWN -> address.down()
            Direction.LEFT -> address.left()
            Direction.UP -> address.up()
            else -> null
        }
    }

    fun removedField(address: List<Address>): Field {
        val removedMasus = masus.map { Pair(it.key, if (address.contains(it.key)) null else it.value) }.associate { it.first to it.second }
        return Field(removedMasus)
    }

    fun moveDown(masus: Map<Address, Direction?>): Map<Address, Direction?> {
//        // 変更のあった要素のMapのリスト
//        val changed: List<Map<Address?, Direction?>> = masus.filter { masus[it.key.down()] == null }.map { mapOf(Pair(it.key, null), Pair(it.key.down(), it.value)) }
//        
//        val changed2 = changed.associate { Pair(it.map { it.key }.first(), it.map { it.value }.first()) }
//        
//        // 変更のあったAddressのリスト
//        val changedAddress = changed.map { listOf(it.first.first, it.second.first) }.flatten()
//        // 変更の無かったAddressのリストとマージ
//        val notChangedAddress = masus.filterNot { changedAddress.contains(it.key) }
//        val downMasus = masus.map { it -> Pair(it.key, if (masus[it.key.down()] == null) null else it.value) }.associate { it.first to it.second }
//        return if (downMasus.filter { downMasus[it.key.down()] == null }.isEmpty()) downMasus else moveDown(downMasus)

        val changed: List<Pair<Address?, Direction?>> = masus.filter { masus[it.key.down()] == null && !it.key.isBottomEdge() }.map { listOf(Pair(it.key, null), Pair(it.key.down(), it.value)) }.flatten()
        val changedMap = changed.associate { it.first!! to it.second }
        return masus.filterNot { changedMap.contains(it.key) }.plus(changedMap)
    }


    companion object FieldFactory {
        fun create5x5Field(rawDirections: List<Int>): Field? {
            if (rawDirections.size != 25) return null
            val directions = rawDirections.map { Direction.of(it) }
            val addresses = (0..4).map { x -> (0..4).map { y -> Address(x, y) } }.flatten()
            val masus = addresses.zip(directions, ::Pair).associate { it.first to it.second }
            return Field(masus)
        }
    }

    fun trace(address: Address): List<Address> {
        return trace(listOf(), address)
    }

    private fun trace(acc: List<Address>, address: Address): List<Address> {
        val next = nextTo(address)
        println(next)
        return when (next) {
            null -> acc
            else -> trace(acc + (next), next)
        }
    }
}
