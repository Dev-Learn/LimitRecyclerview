package dev.tran.nam.library

interface ILimitAdapter {
    var mLimit : Int
    var isSupportLoadBefore : Boolean
    var mTypeLoad : TypeLoad
    var isAfter : Boolean
    var isBefore : Boolean
    var mTypeLoading : TypeLoading
    val isOver : Boolean
    fun updateLoading(type : TypeLoading)
    var errorMessage : String?
    var retry: (() -> Unit)?
}
