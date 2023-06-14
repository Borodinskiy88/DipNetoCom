package com.example.dipnetocom.repository

import com.example.dipnetocom.dto.Job
import kotlinx.coroutines.flow.Flow

interface JobRepository {

    val data: Flow<List<Job>>
    suspend fun getJobsById(id: Int)
    suspend fun save(job: Job)
    suspend fun removeById(id: Int)
}