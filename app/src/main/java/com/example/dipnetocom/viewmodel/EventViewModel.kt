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
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.enumeration.EventType
import com.example.dipnetocom.model.FeedModelState
import com.example.dipnetocom.model.MediaModel
import com.example.dipnetocom.repository.EventRepository
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

private val empty = Event(
    id = 0,
    attachment = null,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    datetime = "",
    published = "",
    coords = null,
    link = null,
    type = EventType.ONLINE,
    participatedByMe = false,
    likedByMe = false,
    likeOwnerIds = emptyList(),
    ownedByMe = false,
    users = emptyMap()
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _stateEvent = MutableLiveData(FeedModelState())
    val stateEvent: LiveData<FeedModelState>
        get() = _stateEvent

    val dataEvent: Flow<PagingData<FeedItem>> = appAuth.authState
        .flatMapLatest { authState ->
            repository.data
                .map { event ->
                    event.map { event ->
                        if (event is Event) {
                            event.copy(ownedByMe = authState?.id == event.authorId)
                        } else {
                            event
                        }
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val editedEvent = MutableLiveData(empty)

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media

    init {
        loadEvents()
    }

    fun changeEventPhoto(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearEventPhoto() {
        _media.value = null
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                _stateEvent.value = (FeedModelState(loading = true))
                repository.getAll()
                _stateEvent.value = FeedModelState()
            } catch (e: Exception) {
                _stateEvent.value = FeedModelState(error = true)
            }
        }
    }

    fun saveEvent() {
        editedEvent.value?.let {
            viewModelScope.launch {
                try {
                    when (val media = media.value) {
                        null -> repository.save(it)
                        else -> {
                            repository.saveWithAttachment(it, media)
                        }
                    }
                    _eventCreated.value = Unit
                    editedEvent.value = empty
                    clearEventPhoto()
                    _stateEvent.value = FeedModelState()
                } catch (e: Exception) {
                    _stateEvent.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun editEvent(event: Event) {
        editedEvent.value = event
    }

    fun changeEventContent(content: String, link: String?) {
        val text = content.trim()
        editedEvent.value = editedEvent.value?.copy(content = text, link = link)
    }

    fun likeEventById(id: Int) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _stateEvent.value = FeedModelState()
            } catch (e: Exception) {
                _stateEvent.value = FeedModelState(error = true)
            }
        }
    }

    fun dislikeEventById(id: Int) {
        viewModelScope.launch {
            try {
                repository.dislikeById(id)
                _stateEvent.value = FeedModelState()
            } catch (e: Exception) {
                _stateEvent.value = FeedModelState(error = true)
            }
        }
    }

    fun removeEventById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _stateEvent.value = FeedModelState()
            } catch (e: Exception) {
                _stateEvent.value = FeedModelState(error = true)
            }
        }
    }

    fun addEventCoordinates(coords: Coordinates) {
        viewModelScope.launch {
            editedEvent.value = editedEvent.value?.copy(coords = coords)
        }
    }

    fun clearEventCoordinates() {
        viewModelScope.launch {
            editedEvent.value = editedEvent.value?.copy(coords = null)
        }
    }
}