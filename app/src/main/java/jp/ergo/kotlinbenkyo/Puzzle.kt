package jp.ergo.kotlinbenkyo

import java.util.Arrays.asList


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


data class Address(val x: Int, val y: Int)
data class Masu(val address: Address, val direction: Direction?)

class Field private constructor(val masus: List<Masu>) {
    fun get(address: Address): Masu? {
        return null
    }

    fun nextTo(address: Address): Masu? {
        return null
    }

    fun remove(masus: List<Masu>): Field {
        return ???
    }

    fun update() {

    }
    
    companion object FieldFactory {
        fun create5x5Field(rawDirections: List<Int>): Field? {
            if(rawDirections.size == 25) return null
            val masus = (0..4).map{x -> (0..4).map{y -> Pair(x,y)}}.flatten().zip(rawDirections.map{Direction.of(it)}){pair, direction -> Masu(Address(pair.first, pair.second), direction)}
            return Field(masus)
        }
    }
    
    fun hoge(address: Address): List<Masu> {
        var address = address
        val list = ArrayList<Masu>()
        while(address != null){
            list.add(get(address)!!)
            address = nextTo(address)!!.address
        }
        return list
    }
    
    fun trace(address:Address): List<Masu> {
        return trace(listOf(), address)
    }
    
    private fun trace(acc: List<Masu>, address: Address): List<Masu> {
        val next = nextTo(address)
        return when (next) {
            null -> acc
            else -> trace(acc + (next), next.address)
        }
    }
}
