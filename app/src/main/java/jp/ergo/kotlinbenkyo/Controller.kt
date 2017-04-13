package jp.ergo.kotlinbenkyo


class Controller {
    companion object {
        fun convertInput(input: String): List<Int> {
            return input.map { it.toString().toInt() }
        }

//        fun x(field: Field): Map<Address, Field>{
//            val availableAddress = field.availableAddress()
//            availableAddress.map{}
//        }
    }
}