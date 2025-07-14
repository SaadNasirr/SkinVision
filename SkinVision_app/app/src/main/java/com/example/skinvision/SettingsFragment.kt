package com.example.skinvision

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skinvision.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("SkinVisionSettings", Context.MODE_PRIVATE)

        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        // Load saved settings
        binding.switchPushNotifications.isChecked = sharedPreferences.getBoolean("push_notifications", true)
        binding.switchEmailNotifications.isChecked = sharedPreferences.getBoolean("email_notifications", false)
        binding.switchDataCollection.isChecked = sharedPreferences.getBoolean("data_collection", true)
        binding.switchDarkMode.isChecked = sharedPreferences.getBoolean("dark_mode", false)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Notification switches
        binding.switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("push_notifications", isChecked).apply()
            requireContext().toast(if (isChecked) "Push notifications enabled" else "Push notifications disabled")
        }

        binding.switchEmailNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("email_notifications", isChecked).apply()
            requireContext().toast(if (isChecked) "Email notifications enabled" else "Email notifications disabled")
        }

        binding.switchDataCollection.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("data_collection", isChecked).apply()
            requireContext().toast(if (isChecked) "Data collection enabled" else "Data collection disabled")
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            requireContext().toast("Dark mode ${if (isChecked) "enabled" else "disabled"} - Restart app to apply")
        }

        // Buttons
        binding.btnChangePassword.setOnClickListener {
            requireContext().toast("Password change feature coming soon!")
        }

        binding.btnClearCache.setOnClickListener {
            // Clear app cache
            requireContext().cacheDir.deleteRecursively()
            requireContext().toast("Cache cleared successfully!")
        }

        binding.btnResetPreferences.setOnClickListener {
            // Reset all preferences to default
            sharedPreferences.edit().clear().apply()
            loadSettings()
            requireContext().toast("Settings reset to default!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
