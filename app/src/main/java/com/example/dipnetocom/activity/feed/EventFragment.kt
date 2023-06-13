package com.example.dipnetocom.activity.feed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.adapter.EventAdapter
import com.example.dipnetocom.adapter.OnInteractionListenerEvent
import com.example.dipnetocom.databinding.FragmentEventBinding
import com.example.dipnetocom.dto.Event
import com.example.dipnetocom.viewmodel.EventViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEventBinding.inflate(inflater, container, false)

        val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
            ?.getString("TOKEN_KEY", null)

        val adapter = EventAdapter(object : OnInteractionListenerEvent {
            override fun onLike(event: Event) {
                if (token == null) {
                    MaterialAlertDialogBuilder(context!!)
                        .setTitle(R.string.log_in_to_account)
                        .setMessage(R.string.please_log_in_to_your_account)
                        .setNegativeButton(R.string.continue_as_a_guest) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.login) { _, _ ->
                            findNavController().navigate(R.id.action_eventFragment_to_loginFragment)
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

            //TODO
            override fun onEdit(event: Event) {
                viewModel.editEvent(event)
                val text = event.content
                val link = event.link
//                val dateTime = event.datetime
                val bundle = Bundle()
                bundle.putString("editedText", text)
                bundle.putString("editedLink", link)
//                bundle.putString("editDate", dateTime)
                findNavController().navigate(R.id.action_eventFragment_to_newEventFragment, bundle)
            }

            override fun onRemove(event: Event) {
                viewModel.removeEventById(event.id)
            }

            //TODO интенты
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

            override fun onAttachment(event: Event) {
                if (event.attachment != null) {
                    findNavController().navigate(
                        R.id.action_eventFragment_to_imageFragment,
                        Bundle().apply { textArg = event.attachment.url }
                    )
                }
            }

            override fun onCoordinates(lat: Double, long: Double) {
                TODO("Not yet implemented")
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