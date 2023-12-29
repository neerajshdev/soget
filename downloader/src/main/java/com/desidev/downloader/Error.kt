package com.desidev.downloader

import java.io.IOException

sealed interface Error {
    class ServerDisAllowed(val statusCode: Int): Error
    class FailedWithIoException(val ex: IOException) : Error
//    object RequestTimeout: Error
}