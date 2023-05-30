package com.example.dipnetocom.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.CardPostBinding
import com.example.dipnetocom.dto.FeedItem
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.utils.ReformatValues.reformatCount
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink

interface OnInteractionListenerPost {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onAttachment(url: String)
    fun onCoordinates(lat: Double, long: Double)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(post: Post) {
        binding.apply {

            post.authorAvatar?.let { avatar.loadCircleCrop(it) }
                ?: avatar.setImageResource(R.drawable.account_circle_24)

            //TODO проверить
            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
                post.attachment?.url?.let { url ->
                    attachment.load(url)
                    attachment.setOnClickListener { onInteractionListener.onAttachment(url) }
                }
            } else {
                attachment.visibility = View.GONE
            }


            author.text = post.author
            job.text = post.authorJob
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = reformatCount(post.likes)
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

fun ImageView.loadCircleCrop(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    load(url, CircleCrop(), *transforms)


fun ImageView.load(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    Glide.with(this)
        .load(url)
        .error(R.drawable.error_24)
        .placeholder(R.drawable.load_24)
        .timeout(10_000)
        .transform(*transforms)
        .into(this)


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