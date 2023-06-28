package com.example.dipnetocom.activity.wall

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.assist.MediaLifecycleObserver
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.activity.feed.JobFragment
import com.example.dipnetocom.activity.map.MapFragment
import com.example.dipnetocom.adapter.OnInteractionListenerPost
import com.example.dipnetocom.adapter.PostAdapter
import com.example.dipnetocom.databinding.FragmentUserBinding
import com.example.dipnetocom.dto.Post
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.view.load
import com.example.dipnetocom.viewmodel.JobViewModel
import com.example.dipnetocom.viewmodel.PostViewModel
import com.example.dipnetocom.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

    companion object {
        const val USER_ID = "USER_ID"
    }

    private val userViewModel by activityViewModels<UserViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val jobViewModel by activityViewModels<JobViewModel>()

    private val mediaObserver = MediaLifecycleObserver()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserBinding.inflate(inflater, container, false)

        val userId = requireArguments().getInt(USER_ID)

        userViewModel.getUserById(userId)
        jobViewModel.getJobsByUserId(userId)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.userWallName.text = user.name
            user.avatar?.let { binding.userWallAvatar.load(it) }
                ?: binding.userWallAvatar.setImageResource(R.drawable.account_circle_24)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest { jobsList ->
                    if (jobsList.isNotEmpty()) {
                        val job = jobViewModel.lastJob(jobsList)
                        binding.userCompany.text = job.name
                    }
                }
            }
        }

        binding.buttonJob.setOnClickListener {
            findNavController().navigate(
                R.id.jobsFragment,
                bundleOf(JobFragment.USER_ID to userId)
            )
        }


        val adapter = PostAdapter(object : OnInteractionListenerPost {
            override fun onLike(post: Post) {
                if (!post.likedByMe) {
                    postViewModel.likeById(post.id)
                } else {
                    postViewModel.dislikeById(post.id)
                }
            }

            override fun onEdit(post: Post) {
                postViewModel.edit(post)
                val bundle = bundleOf(
                    Pair("editedText", post.content),
                    Pair("editedLink", post.link)
                )
                findNavController().navigate(R.id.action_userFragment_to_newPostFragment, bundle)
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
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
                        R.id.action_userFragment_to_imageFragment,
                        Bundle().apply { textArg = post.attachment.url }
                    )
                }
            }

            override fun onCoordinates(lat: Double, long: Double) {
                findNavController().navigate(
                    R.id.action_userFragment_to_mapFragment,
                    bundleOf(
                        Pair(MapFragment.LAT_KEY, lat),
                        Pair(MapFragment.LONG_KEY, long)
                    )
                )
            }

            override fun onAudio(post: Post) {
                if (post.attachment?.type == AttachmentType.AUDIO) {
                    post.attachment?.url?.let { mediaObserver.playPause(it) }
                }
            }

            override fun onVideo(post: Post) {
                if (post.attachment != null) {
                    findNavController().navigate(
                        R.id.action_userFragment_to_videoFragment,
                        Bundle().apply { textArg = post.attachment.url }
                    )
                }
            }
        })

        binding.listContainer.adapter = adapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_newPostFragment)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.wallData(userId).collectLatest { wall ->
                    adapter.submitData(wall)
                }
            }
        }

        userViewModel.userDataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        mediaObserver.player?.setOnCompletionListener {
            mediaObserver.player?.stop()
        }

        return binding.root
    }
}