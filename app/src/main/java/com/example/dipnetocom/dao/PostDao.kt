package com.example.dipnetocom.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.dipnetocom.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPostById(id: Int): PostEntity

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Upsert
    suspend fun save(post: PostEntity)

//TODO

//    suspend fun likedById(id: Int, userId: Int) {
//        val post = getPostById(id)
//        val likeUser = post.likeOwnerIds.toMutableList()
//        likeUser.add(userId)
//        save(post.copy(likedByMe = true, likeOwnerIds = likeUser))
//    }
//
//    suspend fun dislikedById(id: Int, userId: Int) {
//        val post = getPostById(id)
//        val likeUser = post.likeOwnerIds.toMutableList()
//        likeUser.remove(userId)
//        save(post.copy(likedByMe = false, likeOwnerIds = likeUser))
//    }

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()
}