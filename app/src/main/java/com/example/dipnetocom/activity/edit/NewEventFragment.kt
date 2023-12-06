package com.example.dipnetocom.activity.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
class NewEventFragment : Fragment() {

    companion object {
        const val EDITED_TEXT_KEY = "editedText"
        const val EDITED_LINK_KEY = "editedLink"
        const val EDITED_DATE_KEY = "editedDate"
    }

    private val viewModel: EventViewModel by activityViewModels()

    private var _binding: FragmentNewEventBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewEventBinding.inflate(
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
            editText.setText(arguments?.getString(EDITED_TEXT_KEY))
            editLink.setText(arguments?.getString(EDITED_LINK_KEY))
            val dataTime = arguments?.getString(EDITED_DATE_KEY)
            if (dataTime != null) {
                editEventDate.text = reformatDate(dataTime.toString())
                editEventTime.text = reformatTime(dataTime.toString())
            }
        }

        binding.editEventDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val date = reformatDatePicker(calendar.time)
                binding.editEventDate.text = date
            }
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        binding.editEventTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val time = reformatTimePicker(calendar.time)
                binding.editEventTime.text = time
            }
            TimePickerDialog(
                context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
                .show()
        }

        binding.fab.setOnClickListener {
            val link = reformatWebLink(binding.editLink.text.toString())

            val type = if (binding.online.isChecked) {
                EventType.ONLINE
            } else {
                EventType.OFFLINE
            }

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
                editText,
                link.ifEmpty { null }
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

        binding.coordinatesLat.setOnClickListener {
            findNavController().navigate(
                R.id.action_newEventFragment_to_mapFragment,
                bundleOf(
                    Pair(MapFragment.ITEM_TYPE, MapFragment.Companion.ItemType.EVENT.name)
                )
            )
        }

        binding.coordinatesLong.setOnClickListener {
            findNavController().navigate(
                R.id.action_newEventFragment_to_mapFragment,
                bundleOf(
                    Pair(MapFragment.ITEM_TYPE, MapFragment.Companion.ItemType.EVENT.name)
                )
            )
        }

        viewModel.editedEvent.observe(viewLifecycleOwner) {
            binding.coordinatesLat.text = it.coords?.lat ?: ""
            binding.coordinatesLong.text = it.coords?.long ?: ""
        }

        binding.clearPlaceButton.setOnClickListener {
            viewModel.clearEventCoordinates()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}