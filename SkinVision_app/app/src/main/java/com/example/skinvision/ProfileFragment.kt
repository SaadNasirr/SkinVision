package com.example.skinvision

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skinvision.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnEditProfile.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } catch (e: Exception) {
                requireContext().toast("Edit Profile coming soon!")
            }
        }

        binding.btnSettings.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
            } catch (e: Exception) {
                requireContext().toast("Settings coming soon!")
            }
        }

        binding.btnHelpCenter.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_helpCenterFragment)
            } catch (e: Exception) {
                requireContext().toast("Help Center coming soon!")
            }
        }

        // Add logout functionality
        view.findViewById<View>(R.id.btnLogout)?.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_global_welcomeFragment)
            requireContext().toast("Logged out successfully")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
