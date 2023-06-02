package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.adapter.OnInteractionListenerPost
import com.example.dipnetocom.adapter.PostAdapter
import com.example.dipnetocom.databinding.FragmentPostBinding
import com.example.dipnetocom.dto.Post

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(object : OnInteractionListenerPost {
            override fun onLike(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onEdit(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onRemove(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onShare(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onAttachment(url: String) {
                TODO("Not yet implemented")
            }

            override fun onCoordinates(lat: Double, long: Double) {
                TODO("Not yet implemented")
            }

        })

        binding.listPosts.adapter = adapter

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_loginFragment)
        }

        //TODO Не подсвечивает кнопку Posts при возвращении назад
        binding.bottomNavigation.menu.findItem(R.id.posts_menu).isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.posts_menu -> {
                    return@setOnItemSelectedListener true
                }

                R.id.events_menu -> {
                    findNavController().navigate(R.id.action_postFragment_to_eventFragment)
                }
            }
            return@setOnItemSelectedListener true
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_newPostFragment)
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}