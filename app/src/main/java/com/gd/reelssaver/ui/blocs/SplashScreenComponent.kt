package com.gd.reelssaver.ui.blocs

import com.arkivanov.decompose.ComponentContext

interface SplashScreenComponent {
    fun finishSplash()
}

class DefaultSplashScreenComponent(
    componentContext: ComponentContext,
    private val onSplashComplete: () -> Unit
) : SplashScreenComponent,
    ComponentContext by componentContext {

    override fun finishSplash() {
        onSplashComplete()
    }

}


class FakeSplashScreenComponent : SplashScreenComponent {
    override fun finishSplash() {

    }

}