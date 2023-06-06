package com.example.dipnetocom.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.dipnetocom.api.ApiService
import com.example.dipnetocom.dao.PostDao
import com.example.dipnetocom.dao.PostRemoteKeyDao
import com.example.dipnetocom.db.AppDb
import com.example.dipnetocom.entity.PostEntity
import com.example.dipnetocom.entity.PostRemoteKeyEntity
import com.example.dipnetocom.entity.toEntity
import com.example.dipnetocom.enumeration.RemoteKeyType
import com.example.dipnetocom.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val postDao: PostDao,
    private val appDb: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    postRemoteKeyDao.max()?.let { id ->
                        service.getPostsAfter(id, state.config.pageSize)
                    } ?: service.getPostsLatest(state.config.initialLoadSize)
                }

                LoadType.PREPEND -> return MediatorResult.Success(false)

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.getPostsBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.removeAll()
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = RemoteKeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = RemoteKeyType.BEFORE,
                                        id = body.last().id
                                    ),
                                )
                            )
                            postDao.removeAll()
                        } else {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = RemoteKeyType.AFTER,
                                    id = body.first().id
                                )
                            )
                        }
                    }

                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = RemoteKeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                postDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}