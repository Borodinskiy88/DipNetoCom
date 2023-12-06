package com.example.dipnetocom.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.CardEventBinding
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.utils.ReformatValues.reformatCount
import com.example.dipnetocom.utils.ReformatValues.reformatDateTime
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink
import com.example.dipnetocom.view.load
import com.example.dipnetocom.view.loadCircleCrop

interface OnInteractionListenerEvent {
    fun onLike(event: Event)
    fun onEdit(event: Event)
    fun onRemove(event: Event)
    fun onShare(event: Event)
    fun onImage(event: Event)
    fun onCoordinates(lat: Double, long: Double)
    fun onAudio(event: Event)
    fun onVideo(event: Event)
    fun onJoin(event: Event)
}

class EventAdapter(
    private val onInteractionListener: OnInteractionListenerEvent
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(EventDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Event -> R.layout.card_event
            else -> error(R.string.unknown_item_type)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_event -> {
                val binding =
                    CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EventViewHolder(binding, onInteractionListener)
            }

            else -> error("${R.string.unknown_view_type}: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Event -> (holder as? EventViewHolder)?.bind(item)
            else -> error(R.string.unknown_item_type)
        }
    }
}


class EventViewHolder(
    private val binding: CardEventBinding,
    private val onInteractionListener: OnInteractionListenerEvent,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {

            event.authorAvatar?.let { avatar.loadCircleCrop(it) }
                ?: avatar.setImageResource(R.drawable.account_circle_24)

            imageAttachment.visibility = View.GONE
            musicGroup.visibility = View.GONE
            videoGroup.visibility = View.GONE
            if (event.attachment != null) {
                when (event.attachment.type) {
                    AttachmentType.AUDIO -> {
                        musicGroup.visibility = View.VISIBLE
                        playMusic.setOnClickListener { onInteractionListener.onAudio(event) }
                        musicTitle.text = reformatWebLink(event.attachment.url)
                    }

                    AttachmentType.IMAGE -> {
                        imageAttachment.visibility = View.VISIBLE
                        event.attachment.url.let { url ->
                            imageAttachment.load(url)
                            imageAttachment.setOnClickListener { onInteractionListener.onImage(event) }
                        }

                    }

                    AttachmentType.VIDEO -> {
                        videoGroup.visibility = View.VISIBLE
                        event.attachment.url.let { url ->
                            videoAttachment.setOnClickListener { onInteractionListener.onVideo(event) }
                            val uri = Uri.parse(url)
                            videoAttachment.setVideoURI(uri)
                            videoAttachment.setOnPreparedListener { mp ->
                                mp?.setVolume(0F, 0F)
                                mp?.isLooping = true
                                videoAttachment.start()
                            }
                        }
                    }
                }
            }


            author.text = event.author

            if (event.authorJob?.isNotEmpty() == true) {
                job.visibility = View.VISIBLE
                job.text = event.authorJob
            } else {
                job.visibility = View.GONE
            }

            published.text = reformatDateTime(event.published)
            content.text = event.content

            eventCalendarText.text = reformatDateTime(event.datetime)
            eventTypeText.text = event.type.toString()

            like.isChecked = event.likedByMe
            like.text = reformatCount(event.likeOwnerIds.size)
            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            join.isChecked = event.participatedByMe
            join.text = reformatCount(event.participantsIds.size)
            join.setOnClickListener {
                onInteractionListener.onJoin(event)
            }

            link.isVisible = event.link != null
            link.text = event.link?.let { reformatWebLink(it) }

            coordinates.isVisible = event.coords != null
            event.coords?.let { coords ->
                coordinates.setOnClickListener {
                    onInteractionListener.onCoordinates(
                        coords.lat.toDouble(),
                        coords.long.toDouble()
                    )
                }
            }

            menu.isVisible = event.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(event)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(event)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}


class EventDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}