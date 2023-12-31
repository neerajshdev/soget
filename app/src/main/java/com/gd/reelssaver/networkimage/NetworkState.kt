package com.gd.reelssaver

sealed class NetworkState {
    object Available: NetworkState()
    object UnAvailable: NetworkState()
}

