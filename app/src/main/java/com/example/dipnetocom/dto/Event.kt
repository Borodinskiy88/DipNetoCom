package com.example.dipnetocom.dto

data class Event(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: String,
    val likedByMe: Boolean = false,
    val participatedByMe: Boolean = false,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val likes: Int = 0,
) : FeedItem()
