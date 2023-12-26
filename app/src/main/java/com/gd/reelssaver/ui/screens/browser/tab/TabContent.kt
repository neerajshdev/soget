package com.gd.reelssaver.ui.screens.browser.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.HomepageContent
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.WebpageContent

@Composable
fun TabContent(comp: TabComponent) {
    val childStack by comp.child.subscribeAsState()

    Children(stack = childStack, animation = stackAnimation(fade() + scale())) {
        when(val child = it.instance) {
            is ChildComp.Homepage -> HomepageContent(component = child.homePage)
            is ChildComp.Webpage -> WebpageContent(component = child.webpage)
        }
    }
}