package com.example.campusequipmentrentalapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.campusequipmentrentalapp.R
import com.example.campusequipmentrentalapp.data.UserSession
import com.example.campusequipmentrentalapp.data.local.AppDatabase
import com.example.campusequipmentrentalapp.databinding.FragmentLoginBinding
import com.example.campusequipmentrentalapp.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("joinedUserId")?.observe(viewLifecycleOwner) { userId ->
                binding.etLoginId.setText(userId)
            }
        binding.btnJoin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_joinFragment)
        }
        binding.btnLogin.setOnClickListener {
            val userId = binding.etLoginId.text.toString().trim()
            if (userId.isEmpty()) {
                binding.etLoginId.error = getString(R.string.enter_user_id)
                binding.etLoginId.requestFocus()
                return@setOnClickListener
            }
            binding.btnLogin.isEnabled = false
            binding.btnJoin.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val user = AppDatabase.getDatabase(requireContext()).userDao().getUserId(userId)
                    if (user == null) {
                        Toast.makeText(requireContext(), R.string.user_not_registered, Toast.LENGTH_SHORT).show()
                    } else {
                        UserSession.login(User(user.id, user.userId, user.name, user.department, user.role))
                        Toast.makeText(requireContext(), getString(R.string.login_success, user.name), Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT).show()
                } finally {
                    _binding?.btnLogin?.isEnabled = true
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
