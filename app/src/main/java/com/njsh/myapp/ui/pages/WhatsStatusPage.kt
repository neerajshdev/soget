package com.njsh.myapp.ui.pages

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.njsh.myapp.entity.EntityWhatsStatus
import com.njsh.myapp.ui.components.TopAppbar
import com.njsh.myapp.util.ImageLoader
import com.njsh.myapp.util.WhatsStatusLoader
import kotlin.math.log

private const val TAG = "WhatsStatusPage"

@Composable
private fun hasDocumentUri() : Boolean
{
    var returnValue = false
    var context = LocalContext.current
    var pUris = context.contentResolver.persistedUriPermissions
    for (pUri in pUris)
    {
        if (pUri.uri.toString().toLowerCase().endsWith("whatsapp/media"))
        {
            returnValue = true
        }
    }
    return returnValue
}


@Composable
private fun hasRWPerm(): Boolean
{
    var returnValue: Boolean
    var perms: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var context = LocalContext.current
    returnValue = ActivityCompat.checkSelfPermission(context, perms[0]) == PackageManager.PERMISSION_GRANTED &&
    ActivityCompat.checkSelfPermission(context, perms[1]) == PackageManager.PERMISSION_GRANTED
    return returnValue
}


/**
 * Ask for read write permissions
 */
@Composable
private fun AskForRWPerms(onAccept: ()->Unit, onReject: ()-> Unit)
{
    Log.d(TAG, "AskForRWPerms: ")

    var perms: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var resultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = { result ->
        if (result[perms[0]] == true && result[perms[1]] == true)
        {
            onAccept()
        } else
        {
            onReject()
        }
    })

    LaunchedEffect(key1 = Unit)
    {
        resultLauncher.launch(perms);
    }
}


@Composable
private fun AskForDocumentUri(onAccept: () -> Unit, onReject: () -> Unit)
{
    val initialUri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia")
    val context = LocalContext.current
    var resultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = {
        if (it != null)
        {
            if (it.toString().lowercase().endsWith("whatsapp/media"))
            {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                onAccept()
            }
        }else
        {
            onReject()
        }
    })
    resultLauncher.launch(initialUri)
}


fun isScopedStorageEnable(): Boolean = Build.VERSION.SDK_INT > 28


/**
 * Represents the WhatsStatusPage by wrapping composable functions
 */
class WhatsStatusPage
{
    val title: String = "WHATSAPP STATUSES"
    var listOfStatus: MutableState<List<WhatsStatus>?> = mutableStateOf(null)

    private val topAppbar = TopAppbar()

    init {
        topAppbar.title = title
    }

    @Composable
    fun Compose(modifier: Modifier = Modifier)
    {
        val context = LocalContext.current
        Scaffold(
            topBar = {topAppbar.Compose()},
            content = {
                var hasRequiredPerm by remember {mutableStateOf(false) }
                if (isScopedStorageEnable())
                {
                    if (hasDocumentUri())
                    {
                        hasRequiredPerm = true;
                    }
                    else
                    {
                        // ask for uri
                        AskForDocumentUri(
                            onAccept = {hasRequiredPerm = true},
                            onReject = { /* TODO: handle the reject */}
                        )
                    }
                } else
                {
                    if (hasRWPerm())
                    {
                        hasRequiredPerm = true;
                    }
                    else
                    {
                        // ask for read write permissions
                        AskForRWPerms(
                            onAccept = {hasRequiredPerm = true},
                            onReject = { /* TODO: handle the reject */}
                        )
                    }
                }

                if (hasRequiredPerm)
                {
                    // runs the block only the first time when this current composable
                    // enters the composition tree
                    LaunchedEffect(key1 = Unit, block = {
                        if (listOfStatus.value == null)
                        {
                            loadWhatsStatus(context)
                        }
                    })
                }
                ActualContent()
            })
    }


    @Composable
    private fun ActualContent() {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize())
        {
            if (listOfStatus.value != null)
            {
                items(listOfStatus.value!!)
                {
                    it.Compose()
                }
            }
        }
    }



    private fun loadWhatsStatus(context: Context)
    {
        val whatsStatusLoader = if (isScopedStorageEnable())
        {
            WhatsStatusLoader.fromDocumentFile(
                DocumentFile.fromTreeUri(context, whatsMediaUri(context))!!)
        }
        else
        {
            val path = Environment.getExternalStorageDirectory().absolutePath + "Whatsapp/Media/.Statuses"
            WhatsStatusLoader.fromFilepath(path)
        }
        // execute loader here
        val whatsStatuses = whatsStatusLoader()
        listOfStatus.value = whatsStatuses.map { WhatsStatus(it) }
    }

    private fun whatsMediaUri(context: Context): Uri
    {
        val uris = context.contentResolver.persistedUriPermissions
        for (uri in uris)
        {
            if (uri.uri.toString().lowercase().endsWith("whatsapp/media") && uri.isReadPermission)
            {
                return uri.uri
            }
        }
        throw IllegalStateException("No whats uri permissions was taken or persisted")
    }
}



class WhatsStatus(var whatsStatus: EntityWhatsStatus)
{
    var onDownloadClick: (()->Unit)? = null
    var onPlayBtnClick: (()->Unit)? = null
    private var image: Bitmap? = null

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        var topLeftIcon = if (whatsStatus.type == EntityWhatsStatus.Type.VIDEO) com.njsh.myapp.R.drawable.ic_round_video
        else com.njsh.myapp.R.drawable.ic_round_image_24

        var bottomRightIcon = com.njsh.myapp.R.drawable.ic_round_downloaad

        val color: Color = MaterialTheme.colors.secondary
        val tint: Color = MaterialTheme.colors.onSecondary

        BoxWithConstraints(modifier = modifier) {
            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .size(maxWidth)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomEnd = 16.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .border(width = 8.dp, color = color)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // background image
                    if (image != null)
                    {
                        Image(
                            bitmap = image!!.asImageBitmap()
                            , null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // top left icon
                    Icon(
                        painter = painterResource(id = topLeftIcon),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                color = color,
                                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                            )
                            .padding(8.dp)
                    )

                    // bottom right icon button
                    IconButton(
                        onClick = {onDownloadClick?.invoke()},
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(
                                color = color,
                                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = bottomRightIcon),
                            contentDescription = null,
                            tint = tint
                        )
                    }
                }
            }

            LoadImage(reqSize = maxWidth.value.toInt())
        }
    }


    @Composable
    private fun LoadImage(reqSize:Int)
    {
        var context = LocalContext.current
        LaunchedEffect(key1 = Unit) {
            if (whatsStatus.isContentUri) {
                if (whatsStatus.type == EntityWhatsStatus.Type.IMAGE) {
                    ImageLoader.create(
                        Uri.parse(whatsStatus.file),
                        context.contentResolver,
                        reqSize, reqSize
                    ) { bitmap ->
                        image = bitmap
                    }.invoke()
                } else {
                    ImageLoader.createForVideo(
                        Uri.parse(whatsStatus.file),
                        context,
                        100, reqSize, reqSize
                    ) { bitmap ->
                        image = bitmap
                    }
                }
            } else {
                if (whatsStatus.type == EntityWhatsStatus.Type.IMAGE) {
                    ImageLoader.create(
                        whatsStatus.file,
                        reqSize, reqSize
                    ) { bitmap ->
                        image = bitmap
                    }.invoke()
                } else {
                    ImageLoader.createForVideo(
                        whatsStatus.file,
                        100, reqSize, reqSize
                    ) { bitmap ->
                        image = bitmap
                    }
                }
            }
        }
    }
}

