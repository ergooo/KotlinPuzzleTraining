package jp.ergo.kotlinbenkyo


object Logger {
    var enabled = false

    fun d(msg: String) {
        if (enabled) {
            println(msg)
        }
    }
}  
