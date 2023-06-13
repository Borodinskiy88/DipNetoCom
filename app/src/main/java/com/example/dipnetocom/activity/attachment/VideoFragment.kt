package com.example.dipnetocom.activity.attachment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVideoBinding.inflate(inflater, container, false)

        val urlAttachment = arguments?.textArg.toString()

        binding.apply {
            video.setVideoURI(Uri.parse(urlAttachment))
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(video)
            mediaController.setMediaPlayer(video)
            video.setMediaController(mediaController)
            video.start()
        }

        return binding.root
    }

}