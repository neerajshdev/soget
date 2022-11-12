package com.njsh.reelssaver.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.njsh.reelssaver.R

val openSansFamily = FontFamily(
    Font(resId = R.font.opensans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.open_sans_bold, weight = FontWeight.Bold),
    Font(resId = R.font.open_sans_semi_bold, weight = FontWeight.SemiBold),
    Font(resId = R.font.opensans_light, weight = FontWeight.Light),
    Font(resId = R.font.opensans_extra_bold, weight = FontWeight.ExtraBold),
    Font(resId = R.font.opensans_medium, weight = FontWeight.Medium),
    Font(resId = R.font.opensans_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resId = R.font.opensans_italic, style = FontStyle.Italic),
    Font(resId = R.font.opensans_semibold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(resId = R.font.opensans_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(resId = R.font.opensans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resId = R.font.opensans_extrabold_italic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
)

val bowlByOneFamily = FontFamily(
    Font(R.font.bowlby_one_regular)
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)