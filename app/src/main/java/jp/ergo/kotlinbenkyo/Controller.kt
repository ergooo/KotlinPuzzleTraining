package jp.ergo.kotlinbenkyo

import jp.ergo.kotlinbenkyo.Controller.Companion.toCollapsedMap


fun main(args: Array<String>) {
    val input = "0000120011333113332133222"
    val field = Field.FieldFactory.create5x5Field(Controller.convertInput(input))!!
    val collapsedMap = Controller.toCollapsedMap(field)
    
    val path = mutableListOf<Address>()
    
    val hoge = toCollapsedMap(collapsedMap.keys.first())
    path += collapsedMap[collapsedMap.keys.first()]!!
    
    val mage = toCollapsedMap(hoge.keys.first())
    val piyo = toCollapsedMap(mage.keys.first())
    val fuga = toCollapsedMap(piyo.keys.first())
    val hogehoge = toCollapsedMap(fuga.keys.first())
    val magemage = toCollapsedMap(hogehoge.keys.first())
    val piyopiyo = toCollapsedMap(magemage.keys.first())
    val fugafuga = toCollapsedMap(piyopiyo.keys.first())
    val ho = toCollapsedMap(fugafuga.keys.first())
    val mo = toCollapsedMap(ho.keys.first())
    
    if (mo.filter { it.key.isEmpty() }.isNotEmpty()) {
        println(ho.keys.first())
        println("Empty!!")
        return
    }
    
    println(mo.size)
    mo.map {
        println(it.value)
        println(it.key.toArrowSquare())
    }
}

class Controller {
    companion object {
        /**
         * 文字列をIntのリストに変換する
         */
        fun convertInput(input: String): List<Int> {
            return input.map { it.toString().toInt() }
        }

        /**
         * 与えられたFieldに対して有効なAddressそれぞれすべて選択した後の状態をMapで返す。
         */
        fun toCollapsedMap(field: Field): Map<Field, Address> {
            return field.availableAddress().map { field.collapseFrom(it) to it }.toMap()
        }

        tailrec fun ge(field: Field, availables: List<Address>, acc: List<Address>): List<Address> {
            return when {
                field.isEmpty() -> acc
                availables.isEmpty() -> acc
                else -> ge(field.collapseFrom(availables.first()), availables.drop(0), acc + availables.first())
            }
        }
        
        fun getResult(field: Field): List<Address> {
            return listOf()
        }
    }
}
