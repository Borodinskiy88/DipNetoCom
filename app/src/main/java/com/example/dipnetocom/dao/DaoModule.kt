package com.example.dipnetocom.dao

import com.example.dipnetocom.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideWallKeyDao(db: AppDb): WallDao = db.wallDao()

    @Provides
    fun provideWallRemoteKeyDao(db: AppDb): WallRemoteKeyDao = db.wallRemoteKeyDao()

    @Provides
    fun provideEventDao(db: AppDb): EventDao = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()

    @Provides
    fun provideJobDao(db: AppDb): JobDao = db.jobDao()
}