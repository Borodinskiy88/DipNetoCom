package com.example.dipnetocom.dto

data class Post(
    override val id: Long,
    val attachment: Attachment? = null,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val likes: Int,
    val ownedByMe: Boolean = false,
) : FeedItem()