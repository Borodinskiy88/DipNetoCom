package com.example.dipnetocom.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dipnetocom.dao.EventDao
import com.example.dipnetocom.dao.EventRemoteKeyDao
import com.example.dipnetocom.dao.JobDao
import com.example.dipnetocom.dao.PostDao
import com.example.dipnetocom.dao.PostRemoteKeyDao
import com.example.dipnetocom.dao.WallDao
import com.example.dipnetocom.dao.WallRemoteKeyDao
import com.example.dipnetocom.entity.CoordinatesConverter
import com.example.dipnetocom.entity.EventEntity
import com.example.dipnetocom.entity.EventRemoteKeyEntity
import com.example.dipnetocom.entity.ListIntConverter
import com.example.dipnetocom.entity.MapUsersPrevConverter
import com.example.dipnetocom.entity.PostEntity
import com.example.dipnetocom.entity.PostRemoteKeyEntity

@Database(

    entities = [
        PostEntity::class, PostRemoteKeyEntity::class,
//        WallEntity::class, WallRemoteKeyEntity::class,
        EventEntity::class, EventRemoteKeyEntity::class,
//        JobEntity::class

    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(ListIntConverter::class, MapUsersPrevConverter::class, CoordinatesConverter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun wallDao(): WallDao
    abstract fun wallRemoteKeyDao(): WallRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun jobDao(): JobDao
}