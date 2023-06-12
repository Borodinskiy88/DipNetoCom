package com.example.dipnetocom.activity.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentNewEventBinding
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.enumeration.EventType
import com.example.dipnetocom.utils.AndroidUtils
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink
import com.example.dipnetocom.utils.StringArg
import com.example.dipnetocom.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Suppress("UNUSED_EXPRESSION")
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: EventViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
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
                        viewModel.changeEventPhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        binding.apply {
            editText.setText(arguments?.getString("editedText"))
            editLink.setText(arguments?.getString("editedLink"))
            editEventCalendarText.setText(arguments?.getString("editDate"))
        }

        binding.fab.setOnClickListener {
            val link = reformatWebLink(binding.editLink.text.toString())
            //TODO
            val dateTime =
                //            val dateTime = reformatDate(binding.editEventCalendarText.text.toString())

                viewModel.changeEventContent(
                    //  reformatDateTime(binding.editEventCalendarText.text.toString()),
                    binding.editEventCalendarText.text.toString() + "T" + ":00.000000Z",
                    if (binding.online.isChecked) {
                        EventType.ONLINE
                    } else {
                        EventType.OFFLINE
                    },
                    binding.editText.text.toString(),
                    link?.ifEmpty { null }
                )
            viewModel.saveEvent()
            AndroidUtils.hideKeyboard(requireView())
            viewLifecycleOwner
        }

        binding.clear.setOnClickListener {
            viewModel.clearEventPhoto()
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

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            viewModel.loadEvents()
            findNavController().navigateUp()
        }

        viewModel.stateEvent.observe(viewLifecycleOwner) { state ->
            if (state.error)
                Toast.makeText(context, R.string.error_loading, Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
}