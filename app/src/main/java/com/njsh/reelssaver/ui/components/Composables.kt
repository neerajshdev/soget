package com.njsh.reelssaver.ui.components

/*
@Composable
fun RoundedButton(
    modifier: Modifier = Modifier, painter: Painter, label: String, onClick: () -> Unit
) {
    val colors = MaterialTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    100.dp
                )
            )
            .clickable(onClick = onClick)
            .background(color = colors.primary)
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Icon(
            painter = painter, contentDescription = null, tint = colors.onPrimary
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = label, color = colors.onPrimary)
    }
}

@Preview
@Composable
fun PrevRoundedButton() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                RoundedButton(painter = painterResource(id = R.drawable.ic_outlined_instagram),
                    label = "INSTAGRAM",
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(0.8f),
                    onClick = {})
            }
        }
    }
}


@Composable
fun RightCurvedHeading(modifier: Modifier = Modifier, label: String) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
            .background(color = MaterialTheme.colors.primary)
            .padding(vertical = 16.dp, horizontal = 24.dp), contentAlignment = Alignment.CenterEnd
    ) {
        Text(text = label, color = MaterialTheme.colors.onPrimary)
    }
}


@Preview
@Composable
fun PrevRightCurvedHeading() {
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            RightCurvedHeading(modifier = Modifier.padding(16.dp), "ALL VIDEO DOWNLOADER")
        }
    }
}


@Composable
fun CircularProgressBar(isLoading: State<Boolean>) {
    if (isLoading.value) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
        )
    }
}


@Composable
fun NativeAdView(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colors
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    AndroidView(modifier = modifier, factory = {
        val view = LayoutInflater.from(it).inflate(R.layout.native_ad_view, null) as TemplateView
        view.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
    }, update = { view ->
        view.apply {
            val style: NativeTemplateStyle = NativeTemplateStyle.Builder().withMainBackgroundColor(
                ColorDrawable(colors.surface.toArgb())
            ).build()
            if (nativeAd != null) {
                setNativeAd(nativeAd)
                visibility = View.VISIBLE
            } else {
                visibility = View.INVISIBLE
            }
            setStyles(style)
        }
    })

    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = Unit) {
        scope.launch(Dispatchers.IO) {
            nativeAd = NativeAdLoader.takeAndLoad()
        }

        onDispose {
            nativeAd?.let {
                it.destroy()
                nativeAd = null
            }
        }
    }
}
*/
