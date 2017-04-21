package jp.ergo.kotlinbenkyo


fun main(args: Array<String>) {
//    Logger.enabled = true
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


class Controller {
    companion object {

        fun getPath(field: Field): List<Address> {
            return getPath(mapOf(field to listOf()), hashSetOf())
        }

        tailrec fun getPath(collapsedMap: Map<Field, List<Address>>, cache: Set<Field>): List<Address> {
            // collapsedMapのField一つ一つにtoCollapsedMapを適用し、それまで辿ってきたAddressを追加する。得られる結果はListである。
            val collapsedMapList = collapsedMap.map { toCollapsedMap(it.key).mapValues { entry -> it.value + entry.value }.map { it.key to it.value } }.flatten().toMap()
            // collapsedMapListの中にEmptyなやつがいれば即終了
            return collapsedMapList[collapsedMap.keys.first().empty()] ?: getPath(collapsedMapList.filterKeys { !cache.contains(it) }, cache + collapsedMapList.keys)
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
