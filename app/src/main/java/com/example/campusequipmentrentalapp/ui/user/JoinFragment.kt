package com.example.campusequipmentrentalapp.ui.user

import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.campusequipmentrentalapp.R
import com.example.campusequipmentrentalapp.data.local.AppDatabase
import com.example.campusequipmentrentalapp.data.local.UserEntity
import com.example.campusequipmentrentalapp.databinding.FragmentJoinBinding
import com.example.campusequipmentrentalapp.model.UserRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class JoinFragment : Fragment(R.layout.fragment_join) {
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentJoinBinding.bind(view)
        val inputLocales = LocaleList.forLanguageTags("ko-KR,en-US")
        binding.etName.imeHintLocales = inputLocales
        binding.etDepartment.imeHintLocales = inputLocales
        val roles = UserRole.values()
        binding.spinnerRole.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, roles.map { it.label }
        )
        binding.btnBackLogin.setOnClickListener { findNavController().popBackStack() }
        binding.btnJoin.setOnClickListener {
            val fields = listOf(
                binding.etUserId to R.string.enter_user_id,
                binding.etName to R.string.enter_name,
                binding.etDepartment to R.string.enter_department
            )
            val invalidField = fields.firstOrNull { it.first.text.toString().trim().isEmpty() }
            if (invalidField != null) {
                invalidField.first.error = getString(invalidField.second)
                invalidField.first.requestFocus()
                return@setOnClickListener
            }
            val user = UserEntity(
                userId = binding.etUserId.text.toString().trim(),
                name = binding.etName.text.toString().trim(),
                department = binding.etDepartment.text.toString().trim(),
                role = roles[binding.spinnerRole.selectedItemPosition]
            )
            binding.btnJoin.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val inserted = AppDatabase.getDatabase(requireContext()).userDao().insertUserIfAbsent(user)
                    if (inserted) {
                        Toast.makeText(requireContext(), R.string.join_success, Toast.LENGTH_SHORT).show()
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("joinedUserId", user.userId)
                        findNavController().popBackStack()
                    } else {
                        binding.etUserId.error = getString(R.string.duplicate_user_id)
                        binding.etUserId.requestFocus()
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.join_failed, Toast.LENGTH_SHORT).show()
                } finally {
                    _binding?.btnJoin?.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
