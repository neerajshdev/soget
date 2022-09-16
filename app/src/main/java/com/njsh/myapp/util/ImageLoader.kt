package com.njsh.myapp.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class ImageLoader(
    private val runOnComplete: (Bitmap?) -> Unit
) {
    operator fun invoke() {
        CoroutineScope(Dispatchers.IO).launch {
            var image: Bitmap? = null
            try {
                image = loadImage()
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    runOnComplete(image)
                }
            }
        }
    }

    protected abstract fun loadImage(): Bitmap?

    /**
     * Loads an image from a file or a content uri, it returns immediately and returns the result in the callback
     * Loaded image will be nearly scaled down to requested size
     * if it can't load the image you will get a null as result and it also calls the print stack trace.
     */
    companion object {
        fun create(
            filepath: String,
            reqWidth: Int,
            reqHeight: Int,
            runOnComplete: (Bitmap?) -> Unit
        ): ImageLoader {
            return object : ImageLoader(runOnComplete) {
                override fun loadImage(): Bitmap? {
                    return BitmapFactory.Options().run {
                        inJustDecodeBounds = true
                        BitmapFactory.decodeFile(filepath, this)
                        inJustDecodeBounds = false
                        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                        BitmapFactory.decodeFile(filepath, this)?.also {
                            /*console(
                                tag = "load image use case",
                                msg = "loaded image from = $filepath, scaled size = ${it.width} x ${it.height}"
                            )*/
                        }
                    }
                }
            }
        }

        fun create(
            content: Uri,
            contentResolver: ContentResolver,
            reqWidth: Int,
            reqHeight: Int,
            runOnComplete: (Bitmap?) -> Unit
        ): ImageLoader {
            return object : ImageLoader(runOnComplete) {
                override fun loadImage(): Bitmap? {
                    return BitmapFactory.Options().run {
                        inJustDecodeBounds = true
                        contentResolver.openFileDescriptor(content, "r")
                            .use { BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor) }
                        inJustDecodeBounds = false
                        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                        contentResolver.openFileDescriptor(content, "r")
                            .use { BitmapFactory.decodeFileDescriptor(it!!.fileDescriptor) }?.also {
                                /*console(
                                    tag = "LoadContentUseCase",
                                    msg = "loaded image from = $content, scaled size = ${it.width} x ${it.height}"
                                )*/
                            }
                    }
                }
            }
        }

        fun createForVideo(
            filepath: String,
            frameAtms: Long,
            reqWidth: Int,
            reqHeight: Int,
            runOnComplete: (Bitmap?) -> Unit
        ): ImageLoader {
            return object : ImageLoader(runOnComplete) {
                override fun loadImage(): Bitmap? {
                    return MediaMetadataRetriever().run {
                        setDataSource(filepath)
                        getFrameAtTime(frameAtms * 1000).also { release() }
                    }
                }
            }
        }

        fun createForVideo(
            uri: Uri,
            context: Context,
            frameAtms: Long,
            reqWidth: Int,
            reqHeight: Int,
            runOnComplete: (Bitmap?) -> Unit
        ): ImageLoader {
            return object : ImageLoader(runOnComplete) {
                override fun loadImage(): Bitmap? {
                    return MediaMetadataRetriever().run {
                        setDataSource(context, uri)
                        getFrameAtTime(frameAtms * 1000).also { release() }
                    }
                }
            }
        }
    }
}



/**
 * Calculates in sample size for to use with BitmapFactory options
 * see [BitmapFactory.Options.inSampleSize]
 *
 * @param reqWidth the minimum width, you want to scale down your image.
 * @param reqHeight the minimum height, you want to scale down your image.
 */
fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var returnedSample = 1
    var inSample = 2
    while (height / inSample >= reqHeight || width / inSample >= reqWidth) {
        returnedSample = inSample
        inSample *= 2
    }
    //console(tag = "calculateInSampleSize", msg = "returned inSampleSize = $returnedSample for actual size = $width x $height and requested size = $reqWidth x $reqHeight")
    return returnedSample
}