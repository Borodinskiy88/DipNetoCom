package com.example.dipnetocom.activity.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.MediaLifecycleObserver
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.activity.wall.UserFragment
import com.example.dipnetocom.adapter.OnInteractionListenerPost
import com.example.dipnetocom.adapter.PostAdapter
import com.example.dipnetocom.databinding.FragmentPostBinding
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.view.load
import com.example.dipnetocom.viewmodel.AuthViewModel
import com.example.dipnetocom.viewmodel.PostViewModel
import com.example.dipnetocom.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val mediaObserver = MediaLifecycleObserver()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
            ?.getString("TOKEN_KEY", null)


        val adapter = PostAdapter(object : OnInteractionListenerPost {
            override fun onLike(post: Post) {
                if (token == null) {
                    MaterialAlertDialogBuilder(context!!)
                        .setTitle(R.string.log_in_to_account)
                        .setMessage(R.string.please_log_in_to_your_account)
                        .setNegativeButton(R.string.continue_as_a_guest) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.login) { _, _ ->
                            findNavController().navigate(R.id.action_postFragment_to_loginFragment)
                            Snackbar.make(binding.root, R.string.login_exit, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        .show()
                } else {
                    if (!post.likedByMe) {
                        viewModel.likeById(post.id)
                    } else {
                        viewModel.dislikeById(post.id)
                    }
                }
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val bundle = bundleOf(
                    Pair("editedText", post.content),
                    Pair("editedLink", post.link)
                )
                findNavController().navigate(R.id.action_postFragment_to_newPostFragment, bundle)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onImage(post: Post) {
                if (post.attachment != null) {
                    findNavController().navigate(
                        R.id.action_postFragment_to_imageFragment,
                        Bundle().apply { textArg = post.attachment.url }
                    )
                }
            }

            override fun onCoordinates(lat: Double, long: Double) {
                TODO("Not yet implemented")
            }

            override fun onAudio(post: Post) {
                if (post.attachment?.type == AttachmentType.AUDIO) {
                    post.attachment?.url?.let { mediaObserver.playPause(it) }
                }
            }

            override fun onVideo(post: Post) {
                if (post.attachment != null) {
                    findNavController().navigate(
                        R.id.action_postFragment_to_videoFragment,
                        Bundle().apply { textArg = post.attachment.url }
                    )
                }
            }

        })

        binding.listPosts.adapter = adapter

        lifecycleScope.launch {
            viewModel.data.collectLatest { data ->
                adapter.submitData(data)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

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

        //TODO
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
                    R.id.action_postFragment_to_userFragment,
                    bundleOf(UserFragment.USER_ID to user.id)
                )
            }
        }

        return binding.root
    }
}