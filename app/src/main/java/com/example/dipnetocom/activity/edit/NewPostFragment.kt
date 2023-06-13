package com.example.dipnetocom.activity.edit

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
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
@Suppress("UNUSED_EXPRESSION")
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
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

//TODO
        binding.apply {
            editText.setText(arguments?.getString("editedText"))
            editLink.setText(arguments?.getString("editedLink"))
//            val urlImages = arguments?.textArg.toString()
//            binding.photoPreview.load(urlImages)
//            arguments?.getString("attachmentUrl")?.let { photoPreview.load(it) }
        }

//        val imageUrl = arguments?.getString("attachmentUrl")

        binding.fab.setOnClickListener {
            val link = reformatWebLink(binding.editLink.text.toString())
            viewModel.changeContent(
                binding.editText.text.toString(),
                link?.ifEmpty { null }
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            viewLifecycleOwner
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
//            val urlImages = arguments?.textArg.toString()
//            if (urlImages.isNotBlank()) {
//                binding.previewContainer.isVisible = true
//                binding.photoPreview.load(urlImages)
//            }
            if (media == null) {
                binding.previewContainer.isGone = true
                return@observe
            }
            binding.previewContainer.isVisible = true
            binding.photoPreview.setImageURI(media.uri)
            //           binding.photoPreview.setImageURI(imageUrl?.toUri())
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
}