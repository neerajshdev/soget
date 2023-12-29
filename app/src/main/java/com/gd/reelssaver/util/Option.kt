package com.gd.reelssaver.util

sealed interface Option<out T: Any> {
    data class Some<T: Any>(val value: T) : Option<T>
    data object None : Option<Nothing>
}


fun <T: Any> Option<T>.isSome() = this is Option.Some<T>
fun <T: Any> Option<T>.isNone() = this is Option.None

fun <T: Any> Option<T>.or(other: Option<T>): Option<T> {
    return if (this is Option.Some) this else other
}

fun <T: Any> Option<T>.asSome() = this as Option.Some<T>

fun <T: Any> Option<T>.unwrap() = this.asSome().value