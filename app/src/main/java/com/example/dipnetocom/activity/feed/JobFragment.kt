package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.adapter.JobAdapter
import com.example.dipnetocom.adapter.OnInteractionListenerJob
import com.example.dipnetocom.databinding.FragmentJobsBinding
import com.example.dipnetocom.dto.Job
import com.example.dipnetocom.viewmodel.AuthViewModel
import com.example.dipnetocom.viewmodel.JobViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JobFragment : Fragment() {

    companion object {
        const val USER_ID = "USER_ID"
    }

    private val jobViewModel by activityViewModels<JobViewModel>()

    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentJobsBinding.inflate(inflater, container, false)

        val adapter = JobAdapter(object : OnInteractionListenerJob {
            //TODO
            override fun onEdit(job: Job) {
                val name = job.name
                val position = job.position
                val start = job.start
                val finish = job.finish
                val link = job.link
                val bundle = Bundle()
                bundle.putString("editedName", name)
                bundle.putString("editedPosition", position)
                bundle.putString("editedStart", start)
                bundle.putString("editedFinish", finish)
                bundle.putString("editedLink", link)
                jobViewModel.edit(job)
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
            }

            override fun onRemove(job: Job) {
                jobViewModel.removeById(job.id)
            }

        })

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }

        binding.listJobs.adapter = adapter

        val userId = requireArguments().getInt(USER_ID)
        jobViewModel.getJobsByUserId(userId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest {
                    adapter.submitList(it)
                }
            }
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            binding.fab.isVisible = it?.id == userId
        }

        return binding.root
    }
}