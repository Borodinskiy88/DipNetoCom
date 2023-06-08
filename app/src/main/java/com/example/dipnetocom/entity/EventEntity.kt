package com.example.dipnetocom.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dipnetocom.dto.Attachment
import com.example.dipnetocom.dto.Coordinates
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.UserPreview
import com.example.dipnetocom.enumeration.EventType

@Entity
class EventEntity(
    @PrimaryKey
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    @ColumnInfo(name = "event_type")
    val type: EventType,
    val likedByMe: Boolean = false,
    val participatedByMe: Boolean = false,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val likeOwnerIds: List<Int> = emptyList(),
    val users: Map<Int, UserPreview> = emptyMap(),
    @Embedded
    val attachment: Attachment? = null,
) {
    fun toDto() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        datetime = datetime,
        published = published,
        coords = coords,
        type = type,
        likedByMe = likedByMe,
        participatedByMe = participatedByMe,
        link = link,
        ownedByMe = ownedByMe,
        likeOwnerIds = likeOwnerIds,
        users = users,
        attachment = attachment
    )

    companion object {

        fun fromDto(event: Event) = EventEntity(
            id = event.id,
            authorId = event.authorId,
            author = event.author,
            authorAvatar = event.authorAvatar,
            authorJob = event.authorJob,
            content = event.content,
            datetime = event.datetime,
            published = event.published,
            coords = event.coords,
            type = event.type,
            likedByMe = event.likedByMe,
            participatedByMe = event.participatedByMe,
            link = event.link,
            ownedByMe = event.ownedByMe,
            likeOwnerIds = event.likeOwnerIds,
            users = event.users,
            attachment = event.attachment
        )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
