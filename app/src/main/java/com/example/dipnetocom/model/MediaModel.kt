package com.example.dipnetocom.model

import android.net.Uri
import com.example.dipnetocom.enumeration.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri,
    val file: File,
    val type: AttachmentType
)
