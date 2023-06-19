package com.example.dipnetocom.activity.edit

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.activity.DatePickerFragment
import com.example.dipnetocom.databinding.FragmentNewJobBinding
import com.example.dipnetocom.utils.AndroidUtils.hideKeyboard
import com.example.dipnetocom.utils.StringArg
import com.example.dipnetocom.viewmodel.JobViewModel
import java.util.Calendar
import java.util.Locale

class NewJobFragment : Fragment() {

    private val viewModel: JobViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.apply {
            editJobName.setText(arguments?.getString("editedName"))
            editJobPosition.setText(arguments?.getString("editedPosition"))
            editJobStart.setText(arguments?.getString("editedStart"))
            editJobFinish.setText(arguments?.getString("editedFinish"))
            editJobLink.setText(arguments?.getString("editedLink"))
        }


        binding.clearButton.setOnClickListener {
            binding.editJobName.text?.clear()
            binding.editJobPosition.text?.clear()
            binding.editJobStart.text?.clear()
            binding.editJobFinish.text?.clear()
            binding.editJobLink.text?.clear()
        }

        binding.editJobStart.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    binding.editJobStart.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.editJobFinish.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    binding.editJobFinish.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.createButton.setOnClickListener {
            val jobName = binding.editJobName.text.toString()
            val jobPosition = binding.editJobPosition.text.toString()
//                val jobStart = editJobStart.text.toString()
//                val jobFinish = editJobFinish.text?.toString()
            val jobStart = binding.editJobStart.text.toString() + "T00:00:00.000000Z"
            val jobFinish = if (binding.editJobFinish.text?.isNotBlank() == true) {
                binding.editJobFinish.text.toString() + "T00:00:00.000000Z"
            } else null
            val jobLink = binding.editJobLink.text?.toString()

            viewModel.changeContent(jobName, jobPosition, jobStart, jobFinish, jobLink)
            viewModel.save()
            hideKeyboard(requireView())
        }


        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}