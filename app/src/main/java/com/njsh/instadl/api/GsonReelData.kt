package com.njsh.instadl.api


class GsonReelData(
    val items: ArrayList<Item>,

    ) {
    class Item(
        val video_versions: ArrayList<VideoVersion>,
        val video_duration: Float,
        val caption: Caption?,
        val image_versions2 : ImageVersion2.Candidates
    )

    class VideoVersion(
        val type: Int,
        val url: String,
        val width: Int,
        val height: Int,
        val id: String
    )

    class ImageVersion2(
        val url: String,
        val width: Int,
        val height: Int
    ) {
        class Candidates(val candidates: ArrayList<ImageVersion2>)
    }

    class Caption(
        val text: String
    )
}
