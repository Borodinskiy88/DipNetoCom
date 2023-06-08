package com.example.dipnetocom.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.dipnetocom.auth.AppAuth
import com.example.dipnetocom.dto.Coordinates
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.model.FeedModelState
import com.example.dipnetocom.model.MediaModel
import com.example.dipnetocom.repository.PostRepository
import com.example.dipnetocom.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    attachment = null,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = "",
    coords = null,
    link = null,
    mentionIds = emptyList(),
    mentionedMe = false,
    likedByMe = false,
    likeOwnerIds = emptyList(),
    ownedByMe = false,
    users = emptyMap()
)


@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { authState ->
            repository.data
                .map { posts ->
                    posts.map { post ->
                        if (post is Post) {
                            post.copy(ownedByMe = authState?.id == post.authorId)
                        } else {
                            post
                        }
                    }
                }
        }.flowOn(Dispatchers.Default)


    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media

    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearPhoto() {
        _media.value = null
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _state.value = (FeedModelState(loading = true))
                repository.getAll()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            try {
                _state.value = (FeedModelState(refreshing = true))
                repository.getAll()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }


    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    when (val media = media.value) {
                        null -> repository.save(it)
                        else -> {
                            repository.saveWithAttachment(it, media)
                        }
                    }
                    _postCreated.value = Unit
                    edited.value = empty
                    clearPhoto()
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String, link: String?) {
        val text = content.trim()
        edited.value = edited.value?.copy(content = text, link = link)
    }

    fun likeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun dislikeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.dislikeById(id)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    //TODO
    fun addCoordinates(coords: Coordinates) {
        viewModelScope.launch {
            edited.value = edited.value?.copy(coords = coords)
        }
    }

    fun clearCoordinates() {
        viewModelScope.launch {
            edited.value = edited.value?.copy(coords = null)
        }
    }
}
