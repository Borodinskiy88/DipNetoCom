package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.adapter.EventAdapter
import com.example.dipnetocom.adapter.OnInteractionListenerEvent
import com.example.dipnetocom.databinding.FragmentEventBinding
import com.example.dipnetocom.dto.Event

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventBinding.inflate(inflater, container, false)

        val adapter = EventAdapter(object : OnInteractionListenerEvent {
            override fun onLike(event: Event) {
                TODO("Not yet implemented")
            }

            override fun onEdit(event: Event) {
                TODO("Not yet implemented")
            }

            override fun onRemove(event: Event) {
                TODO("Not yet implemented")
            }

            override fun onShare(event: Event) {
                TODO("Not yet implemented")
            }

            override fun onCoordinates(lat: Double, long: Double) {
                TODO("Not yet implemented")
            }

        })

        binding.listEvents.adapter = adapter

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_loginFragment)
        }

        binding.bottomNavigation.menu.findItem(R.id.events_menu).isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.posts_menu -> {
                    findNavController().navigate(R.id.action_eventFragment_to_postFragment)
                }

                R.id.events_menu -> {
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener true
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_newEventFragment)
        }


        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}