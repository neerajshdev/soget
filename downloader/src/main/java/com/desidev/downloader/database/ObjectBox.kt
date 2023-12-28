package com.desidev.downloader.database

import io.objectbox.BoxStore
import java.io.File

object ObjectBox {
    lateinit var store: BoxStore private set
    fun init(dir: File) {
        store = MyObjectBox.builder()
            .directory(dir)
            .build()
    }
}