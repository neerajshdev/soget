package com.njsh.reelssaver.shorts.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.njsh.infinitelist.Datasource
import com.njsh.infinitelist.InfiniteList
import com.njsh.infinitelist.rememberInfiniteListState
import com.njsh.reelssaver.shorts.DataModel
import com.njsh.reelssaver.shorts.data.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


@Composable
fun ScrollableShorts() {
    val state = rememberInfiniteListState()
    InfiniteList(datasource = rememberDatasource(), state = state) { item ->
        when (item) {
            is DataModel.ShortVideoModel -> {
                if (item.shortVideo != null) VideoPlayerView(
                    shortVideo = item.shortVideo!!,
                    state = VideoPlayerState()
                )
            }
        }
    }

    DisposableEffect(key1 = state) {
        val observer = state.observeVisibleItems {

        }
        onDispose {
            state.removeItemsObserver(observer)
        }
    }
}


@Composable
fun rememberDatasource(): Datasource<DataModel> {
    val scope = rememberCoroutineScope()
    val exceptionHandler =CoroutineExceptionHandler() {coroutineContext, throwable -> throwable.printStackTrace() }
    return remember {
        object : Datasource<DataModel>() {
            override fun onFreshData(): List<DataModel> {
                val list = mutableListOf<DataModel.ShortVideoModel>()
                repeat(5) {
                    val model = DataModel.ShortVideoModel(it)
                    list.add(model)
                }

                scope.launch(exceptionHandler) {
                    Repository.clear()
                    val shortVideo = Repository.get(0, 5)
                    for (i in 0..shortVideo.lastIndex) {
                        list[i].shortVideo = shortVideo[i]
                    }
                }
                return list
            }
            override fun onNextOf(item: DataModel): List<DataModel> {
                val offset = item.key + 1
                val list = mutableListOf<DataModel.ShortVideoModel>()
                for (i in offset..offset + 5) {
                    list.add(DataModel.ShortVideoModel(i))
                }
                scope.launch(exceptionHandler) {
                    val shortVideo = Repository.get(offset, 5)
                    for (i in 0..shortVideo.lastIndex) {
                        list[i].shortVideo = shortVideo[i]
                    }
                }
                return list
            }
            override fun onPrevOf(item: DataModel): List<DataModel> {
                val to = item.key - 1
                val offset = (to - 5).coerceAtLeast(0)
                val list = mutableListOf<DataModel.ShortVideoModel>()
                for (i in offset..to) {
                    list.add(DataModel.ShortVideoModel(i))
                }
                scope.launch(exceptionHandler) {
                    val shortVideo = Repository.get(offset, to)
                    for (i in 0..shortVideo.lastIndex) {
                        list[i].shortVideo = shortVideo[i]
                    }
                }
                return list
            }
        }
    }
}
