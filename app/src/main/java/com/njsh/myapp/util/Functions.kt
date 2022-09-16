package com.njsh.myapp.util

import androidx.documentfile.provider.DocumentFile
import java.io.File

fun visitDocumentTree(root: DocumentFile, visitor: (DocumentFile)->Int) {
    val children = root.listFiles()
    for (child in children) {
        if(child.isFile) {
            // question: should stop after visit this file
            if (visitor(child) == 0) break
        } else {
            // question: should explore this directory
            val ans = visitor(child)
            if (ans == 0) {
                // go in and break
                visitDocumentTree(child, visitor)
                break
            } else if (ans == 1) {
                // go in and don't break
                visitDocumentTree(child, visitor)
            } else {
                // skip
//                console(TAG, "skipping dir ${child.name}")
            }
        }
    }
}



fun visitFileTree(root : File, visitor: (File) -> Int) {
    val children = root.listFiles()
    if (children != null)
        for (child in children) {
            if(child.isFile) {
                // question: should stop after visit this file
                if (visitor(child) == 0) break
            } else {
                // question: should explore this directory
                val ans = visitor(child)
                if (ans == 0) {
                    // go in and break
                    visitFileTree(child, visitor)
                    break
                } else if (ans == 1) {
                    // go in and don't break
                    visitFileTree(child, visitor)
                } else {
                    // skip
//                    console(TAG, "skipping dir ${child.name}")
                }
            }
        }
}