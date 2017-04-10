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

}

class Field private constructor(val masus: Map<Address, Direction>) {

    fun nextTo(address: Address): Address? {
        return when (masus[address]) {
            Direction.RIGHT -> address.right()
            Direction.DOWN -> address.down()
            Direction.LEFT -> address.left()
            Direction.UP -> address.up()
            else -> null

        }
    }

    fun remove(address: List<Address>): Field{
        ???
    }

    companion object FieldFactory {
        fun create5x5Field(rawDirections: List<Int>): Field? {
            if (rawDirections.size != 25) return null
            val directions = rawDirections.map { Direction.of(it)!! }
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
