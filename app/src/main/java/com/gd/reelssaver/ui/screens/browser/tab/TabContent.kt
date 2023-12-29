package com.gd.reelssaver.ui.screens.browser.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.HomepageContent
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.WebpageContent

@Composable
fun TabContent(
    comp: TabComponent, modifier: Modifier = Modifier,
    bottomNavBar: @Composable () -> Unit
) {
    val childStack by comp.child.subscribeAsState()

    Children(
        stack = childStack,
        animation = stackAnimation(fade() + scale()),
        modifier = modifier
    ) {
        when (val child = it.instance) {
            is ChildComp.Homepage -> HomepageContent(
                component = child.homePage,
                bottomNavBar = bottomNavBar
            )

            is ChildComp.Webpage -> WebpageContent(
                component = child.webpage,
                bottomNavBar = bottomNavBar
            )
        }
    }
}