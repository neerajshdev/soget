package com.centicbhaiya.getitsocial.util

import androidx.documentfile.provider.DocumentFile
import com.centicbhaiya.getitsocial.model.EntityWhatsStatus
import java.io.File

/**
 * Use case for loading the whatsapp media content from local storage
 * using the file api, calling invoke returns a list of ImageMedia.
 */
abstract class WhatsStatusLoader {
    val statusDirName = ".Statuses"
    protected abstract fun exec(): List<EntityWhatsStatus>
    operator fun invoke() = exec()

    companion object {
        fun fromFilepath(pathToWhatsApp: String): WhatsStatusLoader {
            return object : WhatsStatusLoader() {
                override fun exec(): List<EntityWhatsStatus> {
                    val whatsStatusList = mutableListOf<EntityWhatsStatus>()
                    try {
                        visitFileTree(File(pathToWhatsApp)) { file ->
                            if (file.isDirectory) {
                                if (file.name == statusDirName) return@visitFileTree 0 // visit this dir and stop
                                return@visitFileTree 2 // don't explore
                            } else {
                                if (file.parentFile?.name == statusDirName) {
                                    try {
                                        val whatsStatus = EntityWhatsStatus(file.absolutePath, false, whatsStatusType(file.name))
                                        whatsStatusList.add(whatsStatus)
                                    } catch (ex: IllegalArgumentException) {
                                        ex.printStackTrace() // Todo: remove it from here
                                    }
                                }
                            }
                            1 // don't stop
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    return whatsStatusList
                }
            }
        }


        fun fromDocumentFile(documentFile: DocumentFile): WhatsStatusLoader {
            return object : WhatsStatusLoader() {
                override fun exec(): List<EntityWhatsStatus> {
                    val mediaList = mutableListOf<EntityWhatsStatus>()
                    try {
                        visitDocumentTree(documentFile) { doc ->
                            if (doc.isDirectory) {
                                if (doc.name == statusDirName) return@visitDocumentTree 0 // visit this dir and stop
                                return@visitDocumentTree 2 // don't explore
                            } else {
                                if (doc.parentFile?.name == statusDirName) {
                                    try {
                                        val whatsStatus = EntityWhatsStatus(documentFile.uri.toString(), true, whatsStatusType(documentFile.name!!))
                                        mediaList.add(whatsStatus)
                                    } catch (ex: IllegalArgumentException) {
                                        ex.printStackTrace()
                                    }
                                }
                            }
                            1 // don't stop
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    return mediaList
                }
            }
        }

        /**
         * Use case for loading the saved whatsapp media from
         * the given dir.
         */
        fun savedMedia(dir: String) : WhatsStatusLoader {
            return object : WhatsStatusLoader() {
                override fun exec(): List<EntityWhatsStatus> {
                    val list = mutableListOf<EntityWhatsStatus>()
                    visitFileTree(File(dir)) { file ->
                        if (file.isFile) {
                            try {
//                                list.add(createMediaObject(file))
                            } catch (ex: IllegalArgumentException) {
                                ex.printStackTrace()
                            }
                        }
                        1
                    }
                    return list
                }
            }
        }

        private fun whatsStatusType(name: String): EntityWhatsStatus.Type
        {
            if (name.endsWith(".mp4"))
            {
                return EntityWhatsStatus.Type.VIDEO
            } else if(name.endsWith(".png") || name.endsWith(".jpg"))
            {
                return EntityWhatsStatus.Type.IMAGE
            }
            throw java.lang.IllegalArgumentException("invalid input: name = $name")
        }
    }
}