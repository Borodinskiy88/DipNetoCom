package com.example.dipnetocom.activity.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentLoginBinding
import com.example.dipnetocom.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding.apply {
            registrationLink.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }

            loginButton.setOnClickListener {
                val login = loginLog.text.toString()
                val password = passwordLog.text.toString()
                viewModel.login(login, password)
            }

            clear.setOnClickListener {
                loginLog.text?.clear()
                passwordLog.text?.clear()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.loginButton.isEnabled = !state.loading

            if (state.successfulEntry) {
                findNavController().navigateUp()
            }

            if (state.isBlank) {
                Snackbar.make(binding.root, R.string.is_blank, Snackbar.LENGTH_LONG).show()
            }

            if (state.invalidLoginOrPassword) {
                Snackbar.make(
                    binding.root,
                    R.string.invalid_login_or_password,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG).show()
            }

        }



        return binding.root
    }
}