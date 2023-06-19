import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.dipnetocom.api.ApiService
import com.example.dipnetocom.dao.WallDao
import com.example.dipnetocom.dao.WallRemoteKeyDao
import com.example.dipnetocom.db.AppDb
import com.example.dipnetocom.entity.WallEntity
import com.example.dipnetocom.entity.WallRemoteKeyEntity
import com.example.dipnetocom.entity.toWallEntity
import com.example.dipnetocom.enumeration.RemoteKeyType
import com.example.dipnetocom.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val wallDao: WallDao,
    private val appDb: AppDb,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    val authorId: Int
) : RemoteMediator<Int, WallEntity>() {

    private var id = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallEntity>
    ): MediatorResult {

        if (authorId != id) {
            wallRemoteKeyDao.removeAll()
            wallDao.removeAll()
        }
        id = authorId

        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    service.wallGetLatest(
                        authorId = authorId,
                        count = state.config.initialLoadSize
                    )
                }

                LoadType.PREPEND -> return MediatorResult.Success(false)

                LoadType.APPEND -> {
                    val id = wallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    service.wallGetBefore(
                        authorId = authorId,
                        postId = id,
                        count = state.config.pageSize
                    )
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        wallRemoteKeyDao.removeAll()
                        wallDao.removeAll()
                        wallRemoteKeyDao.insert(
                            listOf(
                                WallRemoteKeyEntity(
                                    type = RemoteKeyType.AFTER,
                                    id = body.first().id,
                                ),
                                WallRemoteKeyEntity(
                                    type = RemoteKeyType.BEFORE,
                                    id = body.last().id
                                ),
                            )
                        )
                    }

                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                type = RemoteKeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                wallDao.insert(body.toWallEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}