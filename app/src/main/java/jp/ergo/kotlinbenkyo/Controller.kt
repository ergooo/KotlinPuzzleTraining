package jp.ergo.kotlinbenkyo

import jp.ergo.kotlinbenkyo.Controller.Companion.getPath


fun main(args: Array<String>) {
    val input = "0000120011333113332133222"
    val field = Field.createField(Controller.convertInput(input))!!

    val path = getPath(mapOf(field to listOf()))
    println(path)
}


class Controller {
    companion object {
        fun getPath(collapsedMap: Map<Field, List<Address>>): List<Address> {
            // collapsedMapのField一つ一つにtoCollapsedMapを適用し、それまで辿ってきたAddressを追加する。得られる結果はListである。
            val collapsedMapList: List<Map<Field, List<Address>>> = collapsedMap.map { toCollapsedMap(it.key).mapValues { entry -> it.value + entry.value } }
            // collapsedMapListの中にEmptyなやつがいれば即終了
            val addressesWithEmptyField = collapsedMapList.filter { it[Field.EMPTY] != null }.firstOrNull()?.get(Field.EMPTY)
            if (addressesWithEmptyField != null) {
                return addressesWithEmptyField
            } else {
                return collapsedMapList.map { getPath(it) }.first()
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
    }
}
