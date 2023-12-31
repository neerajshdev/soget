package com.gd.reelssaver.networkimage

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoThumbLoader(
    val timeUs: Long = 1000,
) : NetworkImageLoader {
    private fun getVideoThumbnail(videoUrl: String, timeUs: Long): Bitmap? {
        val retriever = MediaMetadataRetriever()
        var thumbnail: Bitmap? = null
        try {
            // Set video URL
            retriever.setDataSource(videoUrl, HashMap<String, String>())
            thumbnail =
                retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            // Use this thumbnail Bitmap as needed
        } catch (e: Exception) {
            e.printStackTrace();
            // Handle exceptions
        } finally {
            retriever.release()
        }
        return thumbnail
    }

    override suspend fun load(uri: String, size: IntSize): ImageBitmap? {
        return withContext(Dispatchers.Default) {
            getVideoThumbnail(uri, timeUs)?.let { srcBitmap ->
                Bitmap.createScaledBitmap(srcBitmap, size.width, size.height, true)
            }?.asImageBitmap()
        }
    }
}