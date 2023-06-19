package com.example.dipnetocom.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dipnetocom.entity.WallEntity

@Dao
interface WallDao {

    @Query("SELECT * FROM WallEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, WallEntity>

    @Query("SELECT * FROM WallEntity WHERE id = :id")
    suspend fun getPostById(id: Int): WallEntity?

    @Query("SELECT COUNT(*) == 0 FROM WallEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: WallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<WallEntity>)

    @Query("DELETE FROM WallEntity")
    suspend fun removeAll()

    @Query("DELETE FROM WallEntity WHERE id = :id")
    suspend fun removeById(id: Int)
}