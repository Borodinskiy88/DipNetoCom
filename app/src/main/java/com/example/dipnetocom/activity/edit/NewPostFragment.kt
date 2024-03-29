package com.example.dipnetocom.activity.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.activity.map.MapFragment
import com.example.dipnetocom.databinding.FragmentNewPostBinding
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.utils.AndroidUtils
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink
import com.example.dipnetocom.utils.StringArg
import com.example.dipnetocom.viewmodel.PostViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        const val EDITED_TEXT_KEY = "editedText"
        const val EDITED_LINK_KEY = "editedLink"
    }

    private val viewModel: PostViewModel by activityViewModels()

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.photo_error, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.changePhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        binding.apply {
            editText.setText(arguments?.getString(EDITED_TEXT_KEY))
            editLink.setText(arguments?.getString(EDITED_LINK_KEY))
        }

        binding.fab.setOnClickListener {
            val link = reformatWebLink(binding.editLink.text.toString())
            val editText =
                if (binding.editText.text?.isNotBlank() == true) {
                    binding.editText.text.toString()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.empty_field,
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

            viewModel.changeContent(
                editText,
                link.ifEmpty { null }
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            viewLifecycleOwner
        }

        binding.coordinatesLat.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_mapFragment,
                bundleOf(
                    Pair(MapFragment.ITEM_TYPE, MapFragment.Companion.ItemType.POST.name)
                )
            )
        }

        binding.coordinatesLong.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_mapFragment,
                bundleOf(
                    Pair(MapFragment.ITEM_TYPE, MapFragment.Companion.ItemType.POST.name)
                )
            )
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            binding.coordinatesLat.text = it.coords?.lat ?: ""
            binding.coordinatesLong.text = it.coords?.long ?: ""
        }

        binding.clearPlaceButton.setOnClickListener {
            viewModel.clearCoordinates()
        }

        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        binding.photo.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        binding.gallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(photoLauncher::launch)
        }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.previewContainer.isGone = true
                return@observe
            }
            binding.previewContainer.isVisible = true
            binding.photoPreview.setImageURI(media.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.error)
                Toast.makeText(context, R.string.error_loading, Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}