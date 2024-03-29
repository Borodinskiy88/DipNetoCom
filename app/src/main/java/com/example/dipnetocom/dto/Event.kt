package com.example.dipnetocom.dto

import com.example.dipnetocom.enumeration.EventType

data class Event(
    override val id: Int,
    val attachment: Attachment? = null,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: EventType,
    val likedByMe: Boolean = false,
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean = false,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val likeOwnerIds: List<Int> = emptyList(),
    val users: Map<Int, UserPreview>
) : FeedItem()
