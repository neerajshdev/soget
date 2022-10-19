package com.njsh.instadl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njsh.instadl.ui.theme.AppTheme

// this file contains common compose components

class TopAppbar(title: String = "", val menuIcon: ImageVector? = null)
{
    val title = mutableStateOf(title)
    var onMenuClick: (() -> Unit)? = null


    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        TopAppBar(modifier = modifier.clip(
            RoundedCornerShape(
                bottomStart = 14.dp, bottomEnd = 14.dp
            )
        ), title = { Title() }, actions = {})
    }

    @Composable
    private fun NavIcon()
    {
        IconButton(onClick = { onMenuClick?.invoke() }) {
            Icon(imageVector = menuIcon!!, contentDescription = "menu icon")
        }
    }

    @Composable
    private fun Title()
    {
        Text(text = title.value)
    }
}


@Preview
@Composable
fun TopAppBarPrev()
{
    AppTheme {
        val appBar = TopAppbar().apply {
            title.value = "Sample"
        }
        appBar.Compose()
    }
}


class Drawer
{
    sealed class Items(val text: String)
    {
        object Share : Items("Share this app")
        object RateApp : Items("Rate this app")
        object HowTo : Items("How to use")
    }

    val items = listOf<Items>(Items.HowTo, Items.RateApp, Items.Share)
    var onClose: (() -> Unit)? = null
    var onItemSelect: ((Items) -> Unit)? = null

    @Composable
    fun Compose()
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        ) {
            IconButton(onClick = { onClose?.invoke() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
            for (item in items)
            {
                DrawerMenu(text = item.text,
                    isSelected = false,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onItemSelect?.invoke(item)
                        })
            }
        }
    }

    @Composable
    fun DrawerMenu(text: String, isSelected: Boolean, modifier: Modifier = Modifier)
    {
        val textColor: Color
        val fontWeight: FontWeight
        if (isSelected)
        {
            textColor = MaterialTheme.colors.primary
            fontWeight = FontWeight.Bold
        } else
        {
            textColor = MaterialTheme.colors.onBackground
            fontWeight = FontWeight.Normal
        }

        Column(modifier = modifier) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = fontWeight,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color = textColor.copy(alpha = 0.25f))
            )
        }
    }
}

@Preview
@Composable
fun PrevDrawerMenu()
{
    val drawer = Drawer()
    AppTheme {
        drawer.Compose()
    }
}


class InputUrlField
{
    private val text = mutableStateOf("")
    var onUrlInput: ((String) -> Unit)? = null

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        TextField(value = text.value, onValueChange = { text.value = it }, trailingIcon = {
            IconButton(onClick = { onUrlInput?.invoke(text.value) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
        }, modifier = modifier)
    }
}


class InputPasteAndGet
{
    val text: MutableState<String> = mutableStateOf("");
    var eventOnPasteClick = {}
    var eventOnGetClick = {}
    val hintText = ""

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        Column(modifier = modifier) {
            val colors = MaterialTheme.colors

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 16.sp,
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = colors.onBackground,
                    unfocusedBorderColor = colors.onBackground.copy(alpha = 0.8f),
                    focusedBorderColor = colors.onBackground
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = eventOnPasteClick,
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colors.primary),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.background)
                ) {
                    Text(
                        text = "PASTE",
                        color = colors.primary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = eventOnGetClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "GET",
                        color = colors.onPrimary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PrevInputPasteAndGet()
{
    val comp = InputPasteAndGet()
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 16.dp)
    AppTheme {
        Surface(color = MaterialTheme.colors.background) {
            comp.Compose(modifier)
        }
    }
}







