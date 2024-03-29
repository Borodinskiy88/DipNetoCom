package com.example.dipnetocom.activity.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentNewJobBinding
import com.example.dipnetocom.utils.AndroidUtils.hideKeyboard
import com.example.dipnetocom.utils.ReformatValues.reformatDate
import com.example.dipnetocom.utils.ReformatValues.reformatDatePicker
import com.example.dipnetocom.viewmodel.JobViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class NewJobFragment : Fragment() {

    companion object {
        const val EDITED_NAME_KEY = "editedName"
        const val EDITED_POSITION_KEY = "editedPosition"
        const val EDITED_START_KEY = "editedStart"
        const val EDITED_FINISH_KEY = "editedFinish"
        const val EDITED_LINK_KEY = "editedLink"
    }

    private val viewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.apply {
            editJobName.setText(arguments?.getString(EDITED_NAME_KEY))
            editJobPosition.setText(arguments?.getString(EDITED_POSITION_KEY))

            val start = arguments?.getString(EDITED_START_KEY)
            if (start != null) {
                editJobStart.text = reformatDate(start.toString())
            }

            val finish = arguments?.getString(EDITED_FINISH_KEY)
            if (finish != null) {
                editJobFinish.text = reformatDate(finish.toString())
            }

            editJobLink.setText(arguments?.getString(EDITED_LINK_KEY))
        }

        binding.clearButton.setOnClickListener {
            binding.editJobName.text?.clear()
            binding.editJobPosition.text?.clear()
            binding.editJobStart.text = null
            binding.editJobFinish.text = null
            binding.editJobLink.text?.clear()
        }

        binding.editJobStart.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val date = reformatDatePicker(calendar.time)
                binding.editJobStart.text = date
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

        binding.editJobFinish.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val date = reformatDatePicker(calendar.time)
                binding.editJobFinish.text = date
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

        binding.createButton.setOnClickListener {
            val jobName = binding.editJobName.text.toString()
            val jobPosition = binding.editJobPosition.text.toString()
            val jobStart = if (binding.editJobStart.text?.isNotBlank() == true) {
                binding.editJobStart.text.toString() + "T00:00:00.000000Z"
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.empty_date,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val jobFinish = if (binding.editJobFinish.text?.isNotBlank() == true) {
                binding.editJobFinish.text.toString() + "T00:00:00.000000Z"
            } else null
            val jobLink = binding.editJobLink.text?.toString()

            viewModel.changeContent(
                jobName.ifEmpty { "Unknown" },
                jobPosition.ifEmpty { "Unknown" },
                jobStart,
                jobFinish,
                jobLink?.ifEmpty { null }
            )

            viewModel.save()
            hideKeyboard(requireView())
            viewLifecycleOwner
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}