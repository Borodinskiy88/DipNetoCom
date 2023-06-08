package com.example.dipnetocom.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.dipnetocom.api.ApiService
import com.example.dipnetocom.dao.EventDao
import com.example.dipnetocom.dao.EventRemoteKeyDao
import com.example.dipnetocom.db.AppDb
import com.example.dipnetocom.dto.Attachment
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.dto.Media
import com.example.dipnetocom.entity.EventEntity
import com.example.dipnetocom.entity.toEntity
import com.example.dipnetocom.error.ApiError
import com.example.dipnetocom.error.NetworkError
import com.example.dipnetocom.model.MediaModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = EventRemoteMediator(
            service = apiService,
            appDb = appDb,
            eventDao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao
        ),
        pagingSourceFactory = eventDao::getPagingSource,
    ).flow
        .map { it.map(EventEntity::toDto) }

    override suspend fun getAll() {
        try {
            val response = apiService.getAllEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getById(id: Int): Event {
        return eventDao.getEventById(id).toDto()
    }

    override suspend fun save(event: Event) {
        try {
            val response = apiService.saveEvents(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Int) {
        try {
            val response = apiService.likeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun dislikeById(id: Int) {
        try {
            val response = apiService.dislikeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            val response = apiService.deleteEvent(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            eventDao.removeById(id)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun saveWithAttachment(event: Event, media: MediaModel) {
        try {
            val upLoadMedia = upload(media)
            val response = apiService.saveEvents(
                event.copy(
                    attachment = Attachment(upLoadMedia.url, media.type)
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )

            val response = apiService.uploadMedia(part)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

}