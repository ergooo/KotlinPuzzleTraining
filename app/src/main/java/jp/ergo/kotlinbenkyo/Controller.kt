package jp.ergo.kotlinbenkyo


class Controller {
    companion object {
        fun convertInput(input: String): List<Int> {
            return input.map { it.toString().toInt() }
        }

        fun toCollapsedMap(field: Field): Map<Address, Field>{
            return field.availableAddress().map{it to field.collapseFrom(it)}.toMap()
        }
    }
}