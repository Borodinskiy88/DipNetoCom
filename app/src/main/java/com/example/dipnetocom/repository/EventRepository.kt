package com.example.dipnetocom.repository

import androidx.paging.PagingData
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.model.MediaModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    val data: Flow<PagingData<FeedItem>>
    suspend fun getAll()
    suspend fun getById(id: Int): Event?
    suspend fun save(event: Event)
    suspend fun likeById(id: Int)
    suspend fun dislikeById(id: Int)
    suspend fun removeById(id: Int)
    suspend fun saveWithAttachment(event: Event, media: MediaModel)
    suspend fun joinById(id: Int)
    suspend fun retireById(id: Int)
}