package com.example.dipnetocom.activity.edit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.databinding.FragmentNewJobBinding
import com.example.dipnetocom.utils.AndroidUtils.hideKeyboard
import com.example.dipnetocom.utils.StringArg
import com.example.dipnetocom.viewmodel.JobViewModel

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

        binding.apply {
            clearButton.setOnClickListener {
                viewModel.clearEdited()
            }

            createButton.setOnClickListener {
                val jobName = editJobName.text.toString()
                val jobPosition = editJobPosition.text.toString()
                val jobStart = editJobStart.text.toString()
                val jobFinish = editJobFinish.text?.toString()
                val jobLink = editJobLink.text?.toString()

                viewModel.changeContent(jobName, jobPosition, jobStart, jobFinish, jobLink)
                viewModel.save()
                hideKeyboard(requireView())
            }
        }


        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}