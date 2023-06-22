package com.example.dipnetocom.activity.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipnetocom.R
import com.example.dipnetocom.databinding.FragmentRegistrationBinding
import com.example.dipnetocom.enumeration.AttachmentType
import com.example.dipnetocom.utils.AndroidUtils
import com.example.dipnetocom.view.loadCircleCrop
import com.example.dipnetocom.viewmodel.RegisterViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addPhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        binding.registerAvatar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_avatar)
                .setMessage(R.string.please_choose_avatar)
                .setNegativeButton(R.string.camera) { _, _ ->
                    ImagePicker.with(this)
                        .cameraOnly()
                        .cropSquare()
                        .compress(64)
                        .createIntent(photoLauncher::launch)
                }
                .setPositiveButton(R.string.gallery) { _, _ ->
                    ImagePicker.with(this)
                        .galleryOnly()
                        .cropSquare()
                        .compress(192)
                        .createIntent(photoLauncher::launch)
                }
                .show()
        }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.clearAvatarButton.isVisible = false
                binding.registerAvatar.setImageResource(R.drawable.add_avatar_24)
                return@observe
            } else {
                binding.clearAvatarButton.isVisible = true
                binding.registerAvatar.loadCircleCrop(media.uri.toString())
            }
        }

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

            clearAvatarButton.setOnClickListener {
                viewModel.clearPhoto()
                viewLifecycleOwner
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