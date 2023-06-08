package com.example.dipnetocom.repository

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

//        @Singleton
//        @Binds
//        fun bindsJobRepository(jobRepository: JobRepositoryImpl): JobRepository

    @Singleton
    @Binds
    fun bindsEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsPostRepository(postRepository: PostRepositoryImpl): PostRepository

//        @Singleton
//        @Binds
//        fun bindsUserRepository(userRepository: UserRepositoryImpl): UserRepository

}