package com.example.dipnetocom.activity.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentRegistrationBinding
import com.example.dipnetocom.utils.AndroidUtils
import com.example.dipnetocom.viewmodel.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.apply {

            loginLink.setOnClickListener {
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
            }

            registrationButton.setOnClickListener {
                val login = loginReg.text.toString()
                val password = passwordReg.text.toString()
                val confirmPassword = repeatPasswordReg.text.toString()
                val name = nameReg.text.toString()

                if (password == confirmPassword) {
                    viewModel.register(login, password, name)
                    AndroidUtils.hideKeyboard(requireView())
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.match_passwords,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            clear.setOnClickListener {
                loginReg.text?.clear()
                passwordReg.text?.clear()
                repeatPasswordReg.text?.clear()
                nameReg.text?.clear()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.registrationButton.isEnabled = !state.loading

            if (state.successfulEntry) {
                findNavController().navigateUp()
            }

            if (state.isBlank) {
                Snackbar.make(
                    binding.root,
                    R.string.empty_field,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        return binding.root
    }
}