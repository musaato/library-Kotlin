package com.musashi.library.common


data class Result<T>( // Primary Constructor
    var code: Int,
    var msg: String?=null,
    var data: T? = null
) {
    // Static-like methods in Kotlin are defined inside a companion object
    companion object { // companion object defines a block that belongs to the class itself
        // execution success with an instance returned
        fun <T> success(obj: T): Result<T> {
           return Result(code = 0,msg = "Done successfully", data = obj)
        }

        // execution success without returned data
        fun <T> success(): Result<T>{
            return Result(code = 0, msg = "Done successfully")
        }
        // Error method returns given error message
        fun <T> error(message: String): Result<T> {
            return Result(code = 1, msg = message)
        }
    }
}

