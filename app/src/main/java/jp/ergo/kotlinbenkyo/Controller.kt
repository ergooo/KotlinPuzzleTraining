package jp.ergo.kotlinbenkyo

import jp.ergo.kotlinbenkyo.Controller.Companion.hoge
import jp.ergo.kotlinbenkyo.Controller.Companion.toCollapsedMap


fun main(args: Array<String>) {
    val input = "0000120011333113332133222"
    val field = Field.create5x5Field(Controller.convertInput(input))!!
    val path = hoge(field, Address(0, 0), listOf())
    println(path)


    fun gomi() {
        val input = "0000120011333113332133222"
        val field = Field.create5x5Field(Controller.convertInput(input))!!
        val collapsedMap = Controller.toCollapsedMap(field)

        val path = mutableListOf<Address>()

        var e = collapsedMap.entries.first()
        val hoge = toCollapsedMap(e.key)
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        e = toCollapsedMap(e.key).entries.first()
        path += e.value
        var mo = toCollapsedMap(e.key)
        e = mo.entries.first()
        path += e.value
        if (mo.filter { it.key.isEmpty() }.isNotEmpty()) {
            println("Empty!!")
            println(path)
            return
        }

        println(mo.size)
        mo.map {
            println(it.value)
            println(it.key.toArrowSquare())
        }
    }
}


class Controller {
    companion object {
        fun hoge(field: Field, address: Address, path: List<Address>): List<Address> {
            val nextFields = toCollapsedMap(field)
            val emptyAddress = nextFields[Field.EMPTY]
            return when (emptyAddress) {
                null -> {
                    val e = nextFields.entries.first()
                    hoge(e.key, e.value, path + address)
                }
                else -> path + address + emptyAddress
            }
        }

        fun mage(field: Field): List<Address> {
            val path: List<Address> = listOf()
            val fl = toCollapsedMap(field)
            val emptyAddress = fl[Field.EMPTY]
            when (emptyAddress) {
                null -> {
                    val fl2 = fl.map { toCollapsedMap(it.key) }
                    fl2.forEach { fl ->
                        val emptyAddress = fl[Field.EMPTY]
                        if (emptyAddress != null) {
                            return path + emptyAddress
                        } else {
                            val fl3 = fl.map { toCollapsedMap(it.key) }
                            fl3.forEach {
                                fl ->
                                val emptyAddress = fl[Field.EMPTY]
                                if (emptyAddress != null) {
                                    return path + emptyAddress
                                } else {
                                }
                            }
                        }
                    }
                }
                else -> return path + emptyAddress
            }
        }

        fun hogehoge(fl: Map<Field, Address>, path: List<Address>): List<Address> {
            val flList = fl.map { toCollapsedMap(it.key) }
            flList.forEach { fl ->
                val emptyAddress = fl[Field.EMPTY]
                if (emptyAddress != null) {
                    return@hogehoge path + emptyAddress
                } else {
                    return@hogehoge hogehoge(fl, path)
                }
            }
            

        }

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
