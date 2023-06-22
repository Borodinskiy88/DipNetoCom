package com.example.dipnetocom.activity.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.wall.UserFragment
import com.example.dipnetocom.auth.AppAuth
import com.example.dipnetocom.databinding.FragmentFeedBinding
import com.example.dipnetocom.view.load
import com.example.dipnetocom.viewmodel.AuthViewModel
import com.example.dipnetocom.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

    private companion object {
        const val POSTS_TAG = "POSTS_TAG"
        const val EVENTS_TAG = "EVENTS_TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        if (childFragmentManager.findFragmentById(R.id.container) == null) {
            loadFragment(POSTS_TAG) { PostFragment() }
        }

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

        binding.menuAuth.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_auth)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.login -> {
                            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                            true
                        }

                        R.id.registration -> {
                            findNavController().navigate(R.id.action_feedFragment_to_registrationFragment)
                            true
                        }

                        R.id.logout -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.log_out)
                                .setMessage(R.string.log_out_of_your_account)
                                .setNegativeButton(R.string.no) { dialog, _ ->
                                    dialog.cancel()
                                }
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    appAuth.removeAuth()
                                    authViewModel.data.observe(viewLifecycleOwner) { !authViewModel.authorized }
                                    binding.userName.setText(R.string.anonymous)
                                    Toast.makeText(
                                        requireContext(),
                                        R.string.logged_out_of_your_account,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                                .show()
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }

        authViewModel.data.observe(viewLifecycleOwner) { authModel ->
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