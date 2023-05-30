package com.example.dipnetocom.activity.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dipnetocom.databinding.FragmentNewJobBinding

class NewJobFragment : Fragment() {

    private lateinit var binding: FragmentNewJobBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewJobBinding.inflate(inflater, container, false)

        with(binding) {
            clearButton.setOnClickListener {
                editJobName.text?.clear()
                editJobPosition.text?.clear()
                editJobStart.text?.clear()
                editJobFinish.text?.clear()
            }
        }

        with(binding) {
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