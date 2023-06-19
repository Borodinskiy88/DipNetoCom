package com.example.dipnetocom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dipnetocom.enumeration.RemoteKeyType

@Entity
data class WallRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)