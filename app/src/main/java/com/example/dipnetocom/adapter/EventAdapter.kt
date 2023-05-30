package com.example.dipnetocom.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.CardEventBinding
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.utils.ReformatValues.reformatCount
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink

interface OnInteractionListenerEvent {
    fun onLike(event: Event)
    fun onEdit(event: Event)
    fun onRemove(event: Event)
    fun onShare(event: Event)
    fun onCoordinates(lat: Double, long: Double)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(event: Event) {
        binding.apply {

            event.authorAvatar?.let { avatar.loadCircleCrop(it) }
                ?: avatar.setImageResource(R.drawable.account_circle_24)


            author.text = event.author
            job.text = event.authorJob
            published.text = event.published
            content.text = event.content

            eventCalendarText.text = event.datetime
            eventTypeText.text = event.type

            like.isChecked = event.likedByMe
            like.text = reformatCount(event.likes)
            like.setOnClickListener {
                onInteractionListener.onLike(event)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(event)
            }

            link.isVisible = event.link != null
            link.text = event.link?.let { reformatWebLink(it) }

            coordinates.isVisible = event.coords != null
            event.coords?.let { coords ->
                coordinates.setOnClickListener {
                    onInteractionListener.onCoordinates(
                        coords.lat.toDouble(),
                        coords.lat.toDouble()
                    )
                }
            }

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