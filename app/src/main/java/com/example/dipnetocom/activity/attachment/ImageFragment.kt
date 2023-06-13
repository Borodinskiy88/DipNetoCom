package com.example.dipnetocom.activity.attachment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.activity.edit.NewPostFragment.Companion.textArg
import com.example.dipnetocom.databinding.FragmentImageBinding
import com.example.dipnetocom.view.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(inflater, container, false)

        val urlImages = arguments?.textArg.toString()
        binding.fullscreenImage.load(urlImages)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }


        return binding.root
    }

}