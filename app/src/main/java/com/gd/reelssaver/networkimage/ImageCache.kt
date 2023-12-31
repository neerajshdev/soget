package com.gd.reelssaver.networkimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Collections

class ImageCache {
    private val TAG = "ImageCache"
    private data class Triple (
        val array: ByteArray,
        val timestamp: Long,
        val key: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Triple

            if (!array.contentEquals(other.array)) return false
            if (timestamp != other.timestamp) return false
            if (key != other.key) return false

            return true
        }

        override fun hashCode(): Int {
            var result = array.contentHashCode()
            result = 31 * result + timestamp.hashCode()
            result = 31 * result + key.hashCode()
            return result
        }
    }

    private val keyToTripleMap = Collections.synchronizedMap(HashMap<String, Triple>())
    private val mazSize = 50 * 1024 * 1024 // mega bytes
    private var currentSize = 0



    // return saved image for the key if exists.
    fun get(key: String): Bitmap? {
        val triple = keyToTripleMap.remove(key)
        if (triple != null) {
            return BitmapFactory.decodeByteArray(triple.array, 0, triple.array.size)
        }

        return null
    }


    fun save(bitmap: Bitmap, key: String) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        baos.close()

        val byteArray = baos.toByteArray()
        val timestamp = System.currentTimeMillis()
        val triple = Triple(byteArray, timestamp, key)

        currentSize += byteArray.size
        keyToTripleMap[key] = triple
        if (shouldRemove())removeOld()

        android.util.Log.i(TAG, "${keyToTripleMap.size} were successfully saved! filled: ${currentSize/10_48_576}, max ${mazSize/10_48_576}")
    }


    private fun removeOld() {
        val list = keyToTripleMap.map { it.value }
        val sortedList = list.sortedBy { it.timestamp }

        while (shouldRemove()) {
            val triple = sortedList.first()
            val removed = keyToTripleMap.remove(triple.key)
            if (removed != null) {
                currentSize -= triple.array.size
            }
        }
    }


    private fun shouldRemove() = currentSize > mazSize
}