package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.adapter.JobAdapter
import com.example.dipnetocom.adapter.OnInteractionListenerJob
import com.example.dipnetocom.databinding.FragmentJobsBinding
import com.example.dipnetocom.dto.Job

class JobsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentJobsBinding.inflate(inflater, container, false)

        val adapter = JobAdapter(object : OnInteractionListenerJob {
            override fun onEdit(job: Job) {
                TODO("Not yet implemented")
            }

            override fun onRemove(job: Job) {
                TODO("Not yet implemented")
            }

        })


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }

        binding.listJobs.adapter = adapter

        return binding.root
    }
}