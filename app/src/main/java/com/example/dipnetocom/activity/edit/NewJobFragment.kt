package com.example.dipnetocom.activity.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dipnetocom.databinding.FragmentNewJobBinding
import com.example.dipnetocom.utils.StringArg

class NewJobFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewJobBinding.inflate(inflater, container, false)

        binding.apply {
            clearButton.setOnClickListener {
                editJobName.text?.clear()
                editJobPosition.text?.clear()
                editJobStart.text?.clear()
                editJobFinish.text?.clear()
            }


            createButton.setOnClickListener {
                val jobName = editJobName.text?.toString()
                val jobPosition = editJobPosition.text?.toString()
                val jobStart = editJobStart.text?.toString()
                val jobFinish = editJobFinish.text?.toString()
                //TODO
            }
        }



        return binding.root
    }
}