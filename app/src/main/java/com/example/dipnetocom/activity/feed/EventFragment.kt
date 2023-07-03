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
import com.example.dipnetocom.activity.assist.MediaLifecycleObserver
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.activity.map.MapFragment
import com.example.dipnetocom.adapter.EventAdapter
import com.example.dipnetocom.adapter.OnInteractionListenerEvent
import com.example.dipnetocom.databinding.FragmentEventBinding
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.viewmodel.EventViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventFragment : Fragment() {

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
    }

    private val viewModel: EventViewModel by activityViewModels()

    private val mediaObserver = MediaLifecycleObserver()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEventBinding.inflate(inflater, container, false)

        val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
            ?.getString(TOKEN_KEY, null)

        val adapter = EventAdapter(object : OnInteractionListenerEvent {
            override fun onLike(event: Event) {
                if (token == null) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.log_in_to_account)
                        .setMessage(R.string.please_log_in_to_your_account)
                        .setNegativeButton(R.string.continue_as_a_guest) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.login) { _, _ ->
                            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                            Snackbar.make(binding.root, R.string.login_exit, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        .show()
                } else {
                    if (!event.likedByMe) {
                        viewModel.likeEventById(event.id)
                    } else {
                        viewModel.dislikeEventById(event.id)
                    }
                }
            }

            override fun onEdit(event: Event) {
                viewModel.editEvent(event)
                val bundle = bundleOf(
                    Pair("editedText", event.content),
                    Pair("editedLink", event.link),
                    Pair("editedDate", event.datetime),
                    Pair("url", event.attachment?.url)
                )
                findNavController().navigate(R.id.action_feedFragment_to_newEventFragment, bundle)
            }

            override fun onRemove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onImage(event: Event) {
                if (event.attachment != null) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_imageFragment,
                        Bundle().apply { textArg = event.attachment.url }
                    )
                }
            }

            override fun onCoordinates(lat: Double, long: Double) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_mapFragment,
                    bundleOf(
                        Pair(MapFragment.LAT_KEY, lat),
                        Pair(MapFragment.LONG_KEY, long)
                    )
                )
            }

            override fun onAudio(event: Event) {
                if (event.attachment?.type == AttachmentType.AUDIO) {
                    event.attachment.url.let { mediaObserver.playPause(it) }
                }
            }

            override fun onVideo(event: Event) {
                if (event.attachment != null) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_videoFragment,
                        Bundle().apply { textArg = event.attachment.url }
                    )
                }
            }

            override fun onJoin(event: Event) {
                if (token == null) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.log_in_to_account)
                        .setMessage(R.string.please_log_in_to_your_account)
                        .setNegativeButton(R.string.continue_as_a_guest) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.login) { _, _ ->
                            findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                            Snackbar.make(binding.root, R.string.login_exit, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        .show()
                } else {
                    if (!event.participatedByMe) {
                        viewModel.joinById(event.id)
                    } else {
                        viewModel.retireById(event.id)
                    }
                }
            }

        })

        binding.listEvents.adapter = adapter

        lifecycleScope.launch {
            viewModel.dataEvent.collectLatest { data ->
                adapter.submitData(data)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefresh.isRefreshing = it.refresh is LoadState.Loading
            }
        }

        viewModel.stateEvent.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadEvents() }
                    .show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}