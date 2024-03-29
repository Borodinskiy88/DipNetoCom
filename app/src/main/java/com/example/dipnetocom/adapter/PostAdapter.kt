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
import com.example.dipnetocom.databinding.CardPostBinding
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.utils.ReformatValues.reformatCount
import com.example.dipnetocom.utils.ReformatValues.reformatDateTime
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink
import com.example.dipnetocom.view.load
import com.example.dipnetocom.view.loadCircleCrop

interface OnInteractionListenerPost {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onImage(post: Post)
    fun onCoordinates(lat: Double, long: Double)
    fun onAudio(post: Post)
    fun onVideo(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListenerPost
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Post -> R.layout.card_post
            else -> error(R.string.unknown_item_type)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            else -> error("${R.string.unknown_view_type}: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Post -> (holder as? PostViewHolder)?.bind(item)
            else -> error(R.string.unknown_item_type)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListenerPost,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {

            post.authorAvatar?.let { avatar.loadCircleCrop(it) }
                ?: avatar.setImageResource(R.drawable.account_circle_24)

            imageAttachment.visibility = View.GONE
            musicGroup.visibility = View.GONE
            videoGroup.visibility = View.GONE
            if (post.attachment != null) {
                when (post.attachment.type) {
                    AttachmentType.AUDIO -> {
                        musicGroup.visibility = View.VISIBLE
                        playMusic.setOnClickListener { onInteractionListener.onAudio(post) }
                        musicTitle.text = reformatWebLink(post.attachment.url)
                    }

                    AttachmentType.IMAGE -> {
                        imageAttachment.visibility = View.VISIBLE
                        post.attachment.url.let { url ->
                            imageAttachment.load(url)
                            imageAttachment.setOnClickListener { onInteractionListener.onImage(post) }
                        }
                    }

                    AttachmentType.VIDEO -> {
                        videoGroup.visibility = View.VISIBLE
                        post.attachment.url.let { url ->
                            val uri = Uri.parse(url)
                            videoAttachment.setVideoURI(uri)
                            videoAttachment.setOnPreparedListener { mp ->
                                mp?.setVolume(0F, 0F)
                                mp?.isLooping = true
                                videoAttachment.start()
                            }
                            videoAttachment.setOnClickListener { onInteractionListener.onVideo(post) }
                        }
                    }
                }
            }

            author.text = post.author

            if (post.authorJob?.isNotEmpty() == true) {
                job.visibility = View.VISIBLE
                job.text = post.authorJob
            } else {
                job.visibility = View.GONE
            }

            published.text = reformatDateTime(post.published)
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = reformatCount(post.likeOwnerIds.size)
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            link.isVisible = post.link != null
            link.text = post.link?.let { reformatWebLink(it) }

            coordinates.isVisible = post.coords != null
            post.coords?.let { coords ->
                coordinates.setOnClickListener {
                    onInteractionListener.onCoordinates(
                        coords.lat.toDouble(),
                        coords.long.toDouble()
                    )
                }
            }
            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
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


class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
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