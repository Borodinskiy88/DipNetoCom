package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.wall.UserFragment
import com.example.dipnetocom.databinding.FragmentFeedBinding
import com.example.dipnetocom.view.load
import com.example.dipnetocom.viewmodel.AuthViewModel
import com.example.dipnetocom.viewmodel.UserViewModel

class FeedFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private companion object {
        const val POSTS_TAG = "POSTS_TAG"
        const val EVENTS_TAG = "EVENTS_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        //TODO всегда null
        val save = savedInstanceState
        if (savedInstanceState == null) {
            loadFragment(POSTS_TAG) { PostFragment() }
        }
        binding.bottomNav.menu.findItem(R.id.posts_menu).isChecked = true

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.posts_menu -> {
                    loadFragment(POSTS_TAG) { PostFragment() }
                    true
                }

                R.id.events_menu -> {
                    loadFragment(EVENTS_TAG) { EventFragment() }
                    true
                }

                else -> false
            }
        }

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
        }

        authViewModel.data.observe(viewLifecycleOwner) { authModel ->
            binding.apply {
                if (authViewModel.authorized) {
                    login.setIconResource(R.drawable.logout_24)
                    login.setText(R.string.log_out)
                } else {
                    login.setIconResource(R.drawable.login_24)
                    login.setText(R.string.log_in)
                }
            }
            authModel?.let { userViewModel.getUserById(it.id) }

        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.userName.text = user.name
            user.avatar?.apply {
                binding.userAvatar.load(this)
            } ?: binding.userAvatar.setImageResource(R.drawable.account_circle_24)
            binding.userAvatar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(UserFragment.USER_ID to user.id)
                )
            }
        }

        return binding.root
    }

    private fun loadFragment(tag: String, fragmentFactory: () -> Fragment) {
        val cachedFragment = childFragmentManager.findFragmentByTag(tag)
        val currentFragment = childFragmentManager.findFragmentById(R.id.container)

        if (currentFragment?.tag == tag) return

        childFragmentManager.commit {
            if (currentFragment != null) {
                detach(currentFragment)
            }
            if (cachedFragment != null) {
                attach(cachedFragment)
            } else {
                replace(R.id.container, fragmentFactory(), tag)
            }
        }
    }
}