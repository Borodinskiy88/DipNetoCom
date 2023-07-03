package com.example.dipnetocom.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.dipnetocom.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getEventById(id: Int): EventEntity

    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<EventEntity>)

    @Upsert
    suspend fun save(event: EventEntity)


    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("DELETE FROM EventEntity")
    suspend fun removeAll()
}