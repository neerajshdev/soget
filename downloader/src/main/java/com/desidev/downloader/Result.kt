package com.desidev.downloader


sealed interface Result<out R : Any, out E: Any> {
    class Ok<V : Any>(val value: V): Result<V, Nothing>
    class Err<E: Any>(val err: E ) : Result<Nothing, E>
}