package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_loginFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            //TODO Неактивная кнопка не меняет цвет
            when (item.itemId) {
                R.id.posts_menu -> {
                    findNavController().navigate(R.id.postFragment)
                }

                R.id.events_menu -> {
                    findNavController().navigate(R.id.eventFragment)
                }
            }
            return@setOnItemSelectedListener true
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_newPostFragment)
        }



        return binding.root
    }
}