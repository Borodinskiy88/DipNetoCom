package com.example.dipnetocom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dipnetocom.enumeration.RemoteKeyType

@Entity
class EventRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int
)

