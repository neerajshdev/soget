package com.njsh.instadl.ads

sealed class FullScreenContentBackAction {
    object OnDismissed : FullScreenContentBackAction()
    object OnAdClicked: FullScreenContentBackAction()
    object OnAdFailedToShow: FullScreenContentBackAction()
    object OnAdImpression: FullScreenContentBackAction()
    object OnAdShowed: FullScreenContentBackAction()
}
