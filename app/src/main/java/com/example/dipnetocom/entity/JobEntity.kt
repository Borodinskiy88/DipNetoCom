package com.example.dipnetocom.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dipnetocom.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey
    var id: Int,
    var name: String,
    var position: String,
    var start: String,
    var finish: String? = null,
    var link: String? = null,
    val ownedByMe: Boolean = false
) {
    fun toDto() = Job(
        id = id,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link,
        ownedByMe = ownedByMe
    )

    companion object {
        fun fromDto(job: Job) = JobEntity(
            id = job.id,
            name = job.name,
            position = job.position,
            start = job.start,
            finish = job.finish,
            link = job.link,
            ownedByMe = job.ownedByMe
        )
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(): List<JobEntity> = map(JobEntity::fromDto)