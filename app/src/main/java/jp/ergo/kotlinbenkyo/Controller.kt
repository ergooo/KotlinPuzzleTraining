package jp.ergo.kotlinbenkyo

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.lang.Thread.sleep


fun main(args: Array<String>) {
//    Logger.enabled = true
//    test()

    var finish = true
    launch(CommonPool) {
        bench()
        finish = false
    }
    while (finish) {
        sleep(100)
    }
}

fun test() {
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

suspend fun bench() {
    val input = "0003110131023201033102312"
    val field = Field.createField(Controller.convertInput(input))!!

    var total = 0L
    for (i in (0..10)) {
        val start = System.currentTimeMillis()
        Controller.getPath(field)
        val end = System.currentTimeMillis()
        val diff = end - start
        total += diff
        println("" + (diff.toDouble() / 1000) + " sec")
    }
    println("平均 " + (total.toDouble() / 1000 / 10) + " sec")


}

class Controller {
    companion object {

        suspend fun getPath(field: Field): List<Address> {
            return getPath(mapOf(field to listOf()), hashSetOf())
        }

        suspend fun getPath(collapsedMap: Map<Field, List<Address>>, cache: Set<Field>): List<Address> {

            // collapsedMapのField一つ一つにtoCollapsedMapを適用し、それまで辿ってきたAddressを追加する。
//            val appliedCollapsedMap = collapseSync(collapsedMap, cache)
            val appliedCollapsedMap: Map<Field, List<Address>> = getPathAsync(collapsedMap, cache)
            // collapsedMapListの中にEmptyなやつがいれば即終了
            return appliedCollapsedMap[collapsedMap.keys.first().empty()] ?: getPath(appliedCollapsedMap.filterKeys { !cache.contains(it) }, cache + appliedCollapsedMap.keys)
        }

        suspend fun getPathAsync(collapsedMap: Map<Field, List<Address>>, cache: Set<Field>): Map<Field, List<Address>> {
            if (collapsedMap.size == 1) {
                return collapsedMap
                        .map { toCollapsedMap(it.key, it.value) }
                        .flatten()
                        .toMap()
            }

            val evenRes = collapseAsync(collapsedMap.filterValues { it.first().hashCode() % 2 == 0 }).await()
            val oddRes = collapseAsync(collapsedMap.filterValues { it.first().hashCode() % 2 != 0 }).await()
            return evenRes + oddRes
        }

        fun collapseAsync(collapsedMap: Map<Field, List<Address>>): Deferred<Map<Field, List<Address>>> {
            return async(CommonPool) {
                return@async collapsedMap.map { toCollapsedMap(it.key, it.value) }
                        .flatten()
                        .toMap()
            }
        }

        fun collapseSync(collapsedMap: Map<Field, List<Address>>): Map<Field, List<Address>> {
            return collapsedMap.map { toCollapsedMap(it.key, it.value) }
                    .flatten()
                    .toMap()
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
