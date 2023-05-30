package com.example.dipnetocom.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.CardJobsBinding
import com.example.dipnetocom.dto.Job
import com.example.dipnetocom.utils.ReformatValues.reformatDate
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink

interface Listener {
    fun onEdit(job: Job)
    fun onRemove(job: Job)
}

class JobAdapter(
    private val listener: Listener
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardJobsBinding.inflate(inflater, parent, false)
        return JobViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

class JobViewHolder(
    private val binding: CardJobsBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(job: Job) {
        with(binding) {
            jobName.text = job.name
            jobPosition.text = job.position
            jobStart.text = reformatDate(job.start)
            jobFinish.text = job.finish?.let { reformatDate(it) } ?: ""

            link.isVisible = job.link != null
            link.text = job.link?.let { reformatWebLink(it) }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(job)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(job)
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

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}