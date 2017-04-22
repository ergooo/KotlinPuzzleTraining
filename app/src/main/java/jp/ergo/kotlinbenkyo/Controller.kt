package jp.ergo.kotlinbenkyo

import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
//    Logger.enabled = true
//    test()

    bench()
}

fun test(){
    val input = "0003110131023201033102312"
    val field = Field.createField(Controller.convertInput(input))!!
    println(field.toArrowSquare())

    val actual = field
            .collapseFrom(Address.of(16))
            .collapseFrom(Address.of(16))
            .collapseFrom(Address.of(15))
            .collapseFrom(Address.of(20))
            .collapseFrom(Address.of(11))
            .collapseFrom(Address.of(6))
}

fun bench(){
    val input = "0003110131023201033102312"
    val field = Field.createField(Controller.convertInput(input))!!

    var total = 0L
    for(i in (0..10)){
        val start = System.currentTimeMillis()
        Controller.getPath(field)
        val end = System.currentTimeMillis()
        val diff = end - start
        total += diff
        println(""+(diff.toDouble()/1000) + " sec")
    }
    println("平均 "+(total.toDouble()/1000 / 10) + " sec")


}

class Controller {
    companion object {

        fun getPath(field: Field): List<Address> {
            return getPath(mapOf(field to listOf()), hashSetOf())
        }

        tailrec fun getPath(collapsedMap: Map<Field, List<Address>>, cache: Set<Field>): List<Address> {
            // collapsedMapのField一つ一つにtoCollapsedMapを適用し、それまで辿ってきたAddressを追加する。
            val appliedCollapsedMap =
                    collapsedMap
                            .map { toCollapsedMap(it.key, it.value) }
                            .flatten()
                            .toMap()
            // collapsedMapListの中にEmptyなやつがいれば即終了
            return appliedCollapsedMap[collapsedMap.keys.first().empty()] ?: getPath(appliedCollapsedMap.filterKeys { !cache.contains(it) }, cache + appliedCollapsedMap.keys)
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
        fun toCollapsedMap(field: Field, path: List<Address>): List<Pair<Field, List<Address>>> {
            return field.availableAddress().map { field.collapseFrom(it) to path + it }
        }
    }
}
