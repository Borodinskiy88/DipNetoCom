package com.example.dipnetocom.repository

import androidx.paging.PagingData
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.model.MediaModel
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    val data: Flow<PagingData<FeedItem>>
    fun userWall(id: Int): Flow<PagingData<FeedItem>>
    suspend fun getAll()
    suspend fun getById(id: Int): Post?
    suspend fun save(post: Post)
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun wallRemoveById(id: Int)
}