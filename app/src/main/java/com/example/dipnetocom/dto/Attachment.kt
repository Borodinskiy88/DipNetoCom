package com.example.dipnetocom.dto

import com.example.dipnetocom.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)
