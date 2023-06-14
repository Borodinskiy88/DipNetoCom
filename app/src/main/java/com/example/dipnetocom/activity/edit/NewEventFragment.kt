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
import com.example.dipnetocom.activity.DatePickerFragment
import com.example.dipnetocom.activity.TimePickerFragment
import com.example.dipnetocom.databinding.FragmentNewEventBinding
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.enumeration.EventType
import com.example.dipnetocom.utils.AndroidUtils
import com.example.dipnetocom.utils.ReformatValues.reformatDate
import com.example.dipnetocom.utils.ReformatValues.reformatDatePicker
import com.example.dipnetocom.utils.ReformatValues.reformatTime
import com.example.dipnetocom.utils.ReformatValues.reformatTimePicker
import com.example.dipnetocom.utils.ReformatValues.reformatWebLink
import com.example.dipnetocom.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
@Suppress("UNUSED_EXPRESSION")
class NewEventFragment : Fragment() {

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
            val dataTime = arguments?.getString("editDate")
            editEventDate.text = reformatDate(dataTime.toString())
            editEventTime.text = reformatTime(dataTime.toString())
        }

//        val image = arguments?.getString("url")

        binding.editEventDate.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = reformatDatePicker(selectedDate.time)
                    binding.editEventDate.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.editEventTime.setOnClickListener {
            val timePickerFragment =
                TimePickerFragment { hour, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time = reformatTimePicker(selectedTime.time)
                    binding.editEventTime.text = time
                }
            timePickerFragment.show(childFragmentManager, "timePicker")
        }

        binding.fab.setOnClickListener {
            val link = reformatWebLink(binding.editLink.text.toString())
            val type = if (binding.online.isChecked) {
                EventType.ONLINE
            } else {
                EventType.OFFLINE
            }
            val datetime =
                if (binding.editEventDate.text.isNotBlank() && binding.editEventTime.text.isNotBlank()) {
                    binding.editEventDate.text.toString() + "T" + binding.editEventTime.text.toString() + ":00.000000Z"
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.empty_datetime,
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }


            viewModel.changeEventContent(
                datetime,
                type,
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
            //TODO
//            binding.previewContainer.isVisible = true
//            binding.photoPreview.setImageURI(image?.toUri())
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