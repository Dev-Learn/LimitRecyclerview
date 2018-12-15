package dev.tran.nam.library

interface IHeader<T>{
    val isHeader: Boolean
    val headerValue : T?
}