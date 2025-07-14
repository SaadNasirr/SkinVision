package com.example.skinvision

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skinvision.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var profileManager: ProfileManager

    companion object {
        private const val CAMERA_REQUEST = 1001
        private const val GALLERY_REQUEST = 1002
        private const val CAMERA_PERMISSION_REQUEST = 1003
        private const val STORAGE_PERMISSION_REQUEST = 1004
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        profileManager = ProfileManager(requireContext())

        setupUI()
        loadUserData()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangePhoto.setOnClickListener {
            showPhotoSelectionDialog()
        }

        binding.etDateOfBirth.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun setupUI() {
        // Setup gender dropdown
        val genderOptions = arrayOf("Male", "Female", "Other", "Prefer not to say")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            binding.etEmail.setText(user.email)
            binding.etFullName.setText(user.displayName ?: "")
        }

        // Load saved profile data
        val profileData = profileManager.getProfileData()
        profileData?.let { profile ->
            binding.etFullName.setText(profile.fullName)
            binding.etPhone.setText(profile.phone)
            binding.etDateOfBirth.setText(profile.dateOfBirth)
            binding.etGender.setText(profile.gender, false)

            // Load profile picture
            profile.profileImageBitmap?.let { bitmap ->
                binding.profileImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun showPhotoSelectionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Remove Photo")

        AlertDialog.Builder(requireContext())
            .setTitle("Change Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> checkStoragePermissionAndOpen()
                    2 -> removeProfilePhoto()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            openCamera()
        }
    }

    private fun checkStoragePermissionAndOpen() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(permission), STORAGE_PERMISSION_REQUEST)
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST)
    }

    private fun removeProfilePhoto() {
        binding.profileImage.setImageResource(R.drawable.ic_profile)
        profileManager.removeProfileImage()
        requireContext().toast("Profile photo removed")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                when (requestCode) {
                    CAMERA_REQUEST -> {
                        val bitmap = data?.extras?.get("data") as? Bitmap
                        if (bitmap != null) {
                            setProfileImage(bitmap)
                        } else {
                            requireContext().toast("Failed to capture image")
                        }
                    }
                    GALLERY_REQUEST -> {
                        val imageUri = data?.data
                        if (imageUri != null) {
                            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                            setProfileImage(bitmap)
                        } else {
                            requireContext().toast("Failed to load image from gallery")
                        }
                    }
                }
            } catch (e: Exception) {
                requireContext().toast("Error processing image: ${e.message}")
            }
        }
    }

    private fun setProfileImage(bitmap: Bitmap) {
        // Resize bitmap to reasonable size
        val resizedBitmap = resizeBitmap(bitmap, 300, 300)
        binding.profileImage.setImageBitmap(resizedBitmap)
        profileManager.saveProfileImage(resizedBitmap)
        requireContext().toast("Profile photo updated!")
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    requireContext().toast("Camera permission is required to take photos")
                }
            }
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    requireContext().toast("Storage permission is required to access gallery")
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDateOfBirth.setText(date)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            return
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return
        }

        // Save profile data
        profileManager.saveProfileData(fullName, email, phone, dateOfBirth, gender)

        requireContext().toast("Profile updated successfully!")
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
