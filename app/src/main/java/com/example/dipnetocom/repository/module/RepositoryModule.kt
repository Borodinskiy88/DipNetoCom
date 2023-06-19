package com.example.dipnetocom.repository.module

import com.example.dipnetocom.repository.AuthRepository
import com.example.dipnetocom.repository.AuthRepositoryImpl
import com.example.dipnetocom.repository.EventRepository
import com.example.dipnetocom.repository.EventRepositoryImpl
import com.example.dipnetocom.repository.JobRepository
import com.example.dipnetocom.repository.JobRepositoryImpl
import com.example.dipnetocom.repository.PostRepository
import com.example.dipnetocom.repository.PostRepositoryImpl
import com.example.dipnetocom.repository.UserRepository
import com.example.dipnetocom.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindsJobRepository(jobRepository: JobRepositoryImpl): JobRepository

    @Singleton
    @Binds
    fun bindsEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsPostRepository(postRepository: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsUserRepository(userRepository: UserRepositoryImpl): UserRepository

}