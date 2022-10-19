package com.njsh.instadl.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.njsh.instadl.App
import java.io.File

private const val TAG = "Functions.Kt"

fun visitDocumentTree(root: DocumentFile, visitor: (DocumentFile) -> Int)
{
    if (!root.exists())
    {
        throw IllegalArgumentException("provided document path does not exists")
    }
    val children = root.listFiles()
    for (child in children)
    {
        if (child.isFile)
        {
            // question: should stop after visit this file
            if (visitor(child) == 0) break
        } else
        {
            // question: should explore this directory
            val ans = visitor(child)
            if (ans == 0)
            {
                // go in and break
                visitDocumentTree(child, visitor)
                break
            } else if (ans == 1)
            {
                // go in and don't break
                visitDocumentTree(child, visitor)
            } else
            {
                // skip
//                console(TAG, "skipping dir ${child.name}")
            }
        }
    }
}


fun visitFileTree(root: File, visitor: (File) -> Int)
{
    if (!root.exists())
    {
        throw IllegalArgumentException("provided file path does not exists")
    }
    val children = root.listFiles()
    if (children != null) for (child in children)
    {
        if (child.isFile)
        {
            // question: should stop after visit this file
            if (visitor(child) == 0) break
        } else
        {
            // question: should explore this directory
            val ans = visitor(child)
            if (ans == 0)
            {
                // go in and break
                visitFileTree(child, visitor)
                break
            } else if (ans == 1)
            {
                // go in and don't break
                visitFileTree(child, visitor)
            } else
            {
                // skip
//                    console(TAG, "skipping dir ${child.name}")
            }
        }
    }
}


fun storagePermission(context: Context)
{
    Dexter.withContext(context).withPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ).withListener(object : MultiplePermissionsListener
    {
        override fun onPermissionsChecked(p0: MultiplePermissionsReport?)
        {
            Log.d(TAG, "onPermissionsChecked: ${p0?.grantedPermissionResponses}")
        }

        override fun onPermissionRationaleShouldBeShown(
            p0: MutableList<PermissionRequest>?, p1: PermissionToken?
        )
        {
            TODO("Not yet implemented")
        }

    }).check()
}


fun checkStoragePermission(): Boolean
{
    var result = false
    result = ActivityCompat.checkSelfPermission(
        App.instace(), Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        App.instace(), Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    return result
}


