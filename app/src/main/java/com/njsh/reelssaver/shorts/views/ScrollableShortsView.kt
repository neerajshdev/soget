package com.njsh.reelssaver.shorts.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.njsh.infinitelist.VerticalList


private const val TAG = "ScrollableShortsView.kt"

@Composable
fun ScrollableShorts() {
    VerticalList {
        items(listOf(0x898989, 0x9999887 , 0x9991ff, 0xff1155, 0xff99005)) {
            val isCenter = remember {
                mutableStateOf(false)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(360.dp)
                    .background(
                        color = androidx.compose.ui.graphics
                            .Color(it)
                            .copy(alpha = 1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "isCenter: ${isCenter.value}")
            }

            onLayout {
                isCenter.value = viewport.height/2 in y..y+ height
            }
        }

        onEndOfFrame {

        }

        handleDrag {

        }
    }

/*
    LazyColumn() {
        items(10) {
            val random = remember {
                Random.nextInt(100)
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(color = Color(random)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$random")
            }
        }
    }*/
}

/*
@Composable
fun rememberDatasource(): Datasource<DataModel> {
    return remember {
        object : Datasource<DataModel>() {
            override suspend fun onFreshData(): List<DataModel> {
                var count = 0
                return Repository.get(0, 5).map {
                    DataModel.ShortVideoModel(count++).apply {
                        shortVideo = it
                    }
                }
            }
            override suspend fun onNextOf(item: DataModel): List<DataModel> {
                var count = 0
                val offset = item.key + 1
                return Repository.get(offset, offset + 5).map {
                    DataModel.ShortVideoModel(offset + count++).apply {
                        shortVideo = it
                    }
                }
            }
            override suspend fun onPrevOf(item: DataModel): List<DataModel> {
                val to = item.key - 1
                var offset = (to - 5).coerceAtLeast(0)
                if (to == 0) return emptyList()
                return Repository.get(offset, to).map {
                    DataModel.ShortVideoModel(offset++).apply {
                        shortVideo = it
                    }
                }
            }
        }
    }
}*/
