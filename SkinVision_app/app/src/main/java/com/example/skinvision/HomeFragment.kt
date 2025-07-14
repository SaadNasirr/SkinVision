package com.example.skinvision

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skinvision.databinding.FragmentHomeBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val SERVER_IP = "10.4.73.51"
    private val API_URL = "http://$SERVER_IP:5000/predict"
    private val TAG = "HomeFragment"

    companion object {
        private const val CAMERA_REQUEST = 1888
        private const val GALLERY_REQUEST = 1889
        private const val CAMERA_PERMISSION_REQUEST = 100
        private const val STORAGE_PERMISSION_REQUEST = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "HomeFragment created, API URL: $API_URL")
        requireContext().toast("HomeFragment loaded")

        binding.btnOpenCamera.setOnClickListener {
            Log.d(TAG, "Camera button clicked")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
            } else {
                openCamera()
            }
        }

        binding.btnChooseImage.setOnClickListener {
            Log.d(TAG, "Gallery button clicked")
            checkGalleryPermission()
        }

        binding.btnChat.setOnClickListener {
            Log.d(TAG, "Chat button clicked")
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
        }

        binding.btnDoctors.setOnClickListener {
            Log.d(TAG, "Doctors button clicked")
            findNavController().navigate(R.id.action_homeFragment_to_doctorsFragment)
        }

        binding.btnDirectTest.setOnClickListener {
            Log.d(TAG, "Direct test button clicked")
            testDirectConnection()
        }

        binding.btnTestNetwork.setOnClickListener {
            Log.d(TAG, "Test Network button clicked")
            binding.textDiagnosis.text = "🔧 NETWORK DIAGNOSTICS\n\n📱 Phone IP: Getting...\n💻 Server IP: 192.168.100.122\n🌐 Testing connection...\n\nPlease wait..."
            testDirectConnection()
        }
    }

    private fun checkGalleryPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_REQUEST)
                } else {
                    openGallery()
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST)
                } else {
                    openGallery()
                }
            }
            else -> {
                openGallery()
            }
        }
    }

    private fun openCamera() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                requireContext().toast("Camera not available")
            }
        } catch (e: Exception) {
            requireContext().toast("Error opening camera: ${e.message}")
        }
    }

    private fun openGallery() {
        try {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"

            if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(galleryIntent, GALLERY_REQUEST)
            } else {
                val alternativeIntent = Intent(Intent.ACTION_GET_CONTENT)
                alternativeIntent.type = "image/*"
                alternativeIntent.addCategory(Intent.CATEGORY_OPENABLE)

                if (alternativeIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(Intent.createChooser(alternativeIntent, "Select Image"), GALLERY_REQUEST)
                } else {
                    requireContext().toast("No gallery app found")
                }
            }
        } catch (e: Exception) {
            requireContext().toast("Error opening gallery: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                when (requestCode) {
                    CAMERA_REQUEST -> {
                        val bitmap = data?.extras?.get("data") as? Bitmap
                        if (bitmap != null) {
                            binding.imagePreview.setImageBitmap(bitmap)
                            classifyImage(bitmap)
                        } else {
                            requireContext().toast("Failed to capture image")
                            binding.textDiagnosis.text = "Failed to capture image. Please try again."
                        }
                    }
                    GALLERY_REQUEST -> {
                        val imageUri = data?.data
                        if (imageUri != null) {
                            try {
                                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                                if (bitmap != null) {
                                    binding.imagePreview.setImageBitmap(bitmap)
                                    classifyImage(bitmap)
                                } else {
                                    requireContext().toast("Failed to load image")
                                }
                            } catch (e: Exception) {
                                requireContext().toast("Failed to load image: ${e.message}")
                                binding.textDiagnosis.text = "Failed to load image. Please try again."
                            }
                        } else {
                            requireContext().toast("No image selected")
                        }
                    }
                }
            } catch (e: Exception) {
                requireContext().toast("Error processing image: ${e.message}")
                binding.textDiagnosis.text = "Error processing image. Please try again."
            }
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        binding.textDiagnosis.text = "Connecting to AI server...\nServer: $SERVER_IP:5000\nPreparing image for analysis..."

        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            binding.textDiagnosis.text = "Image prepared successfully\nSize: ${imageBytes.size} bytes\nSending to server: $SERVER_IP:5000\nPlease wait..."

            sendImageToAPI(base64Image)

        } catch (e: Exception) {
            binding.textDiagnosis.text = "Error preparing image: ${e.message}"
            requireContext().toast("Error preparing image")
        }
    }

    private fun sendImageToAPI(base64Image: String) {
        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                val json = JSONObject()
                json.put("image", base64Image)

                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json.toString()
                )

                val request = Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build()

                activity?.runOnUiThread {
                    binding.textDiagnosis.text = "Request sent to server\nURL: $API_URL\nWaiting for response...\n\nIf this takes too long, check:\n- Server is running\n- Same WiFi network\n- Firewall settings"
                }

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity?.runOnUiThread {
                            val errorMessage = "Connection Failed\n\nError: ${e.message}\nServer: $API_URL\nError Type: ${e.javaClass.simpleName}\n\nTroubleshooting:\n1. Check if server is running\n2. Verify IP address: $SERVER_IP\n3. Ensure same WiFi network\n4. Check Windows Firewall\n5. Try: http://$SERVER_IP:5000 in phone browser"

                            binding.textDiagnosis.text = errorMessage
                            requireContext().toast("Connection failed: ${e.javaClass.simpleName}")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()

                        activity?.runOnUiThread {
                            if (response.isSuccessful && responseBody != null) {
                                try {
                                    val result = JSONObject(responseBody)

                                    if (result.has("success") && result.getBoolean("success")) {
                                        val prediction = result.getString("prediction")
                                        val confidence = result.getDouble("confidence")

                                        val resultMessage = "AI Analysis Complete\n\nPrediction: $prediction\nConfidence: ${confidence.toInt()}%\n\n${getConfidenceMessage(confidence.toInt())}\n\nAnalyzed by your trained model\n\nTap 'Doctors' to find specialists\nUse 'Chat' for more information"

                                        binding.textDiagnosis.text = resultMessage
                                        requireContext().toast("Analysis complete!")

                                    } else {
                                        val error = result.optString("error", "Unknown error")
                                        val errorMessage = "Analysis failed: $error"
                                        binding.textDiagnosis.text = errorMessage
                                        requireContext().toast("Analysis failed")
                                    }
                                } catch (e: Exception) {
                                    val errorMessage = "Response Parsing Error\n\nError: ${e.message}\nResponse: $responseBody\n\nThe server responded but the format was unexpected."
                                    binding.textDiagnosis.text = errorMessage
                                    requireContext().toast("Response parsing error")
                                }
                            } else {
                                val errorMessage = "Server Error\n\nHTTP Code: ${response.code}\nMessage: ${response.message}\nResponse: ${responseBody ?: "No response body"}\n\nThe server is reachable but returned an error."
                                binding.textDiagnosis.text = errorMessage
                                requireContext().toast("Server error: ${response.code}")
                            }
                        }
                    }
                })

            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.textDiagnosis.text = "Unexpected Error\n\nError: ${e.message}\nType: ${e.javaClass.simpleName}\n\nPlease try again or check the server."
                    requireContext().toast("Unexpected error")
                }
            }
        }
    }

    private fun getConfidenceMessage(confidence: Int): String {
        return when {
            confidence >= 80 -> "High confidence result"
            confidence >= 60 -> "Moderate confidence result"
            else -> "Low confidence - consider retaking photo"
        }
    }

    private fun testDirectConnection() {
        binding.textDiagnosis.text = "🔍 QUICK CONNECTION TEST\n\nTesting: http://192.168.100.122:5000\n\n⏱️ 3 second timeout..."

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("http://192.168.100.122:5000")
                    .build()

                val startTime = System.currentTimeMillis()
                val response = client.newCall(request).execute()
                val endTime = System.currentTimeMillis()
                val body = response.body?.string()

                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        binding.textDiagnosis.text = "✅ CONNECTION SUCCESS!\n\nTime: ${endTime - startTime}ms\nStatus: ${response.code}\n\nResponse: ${body?.take(100)}...\n\n🎉 SERVER IS WORKING!\n\nNow try chat or camera!"
                        requireContext().toast("✅ CONNECTION WORKS!")
                    } else {
                        binding.textDiagnosis.text = "❌ SERVER ERROR\n\nStatus: ${response.code}\nResponse: $body"
                        requireContext().toast("Server error: ${response.code}")
                    }
                }

            } catch (e: java.net.ConnectException) {
                activity?.runOnUiThread {
                    binding.textDiagnosis.text = "❌ CONNECTION REFUSED\n\n🔥 SERVER NOT RUNNING!\n\nError: ${e.message}\n\n📋 TO FIX:\n1. Start your Python server\n2. Check IP: ipconfig\n3. Disable Windows Firewall"
                    requireContext().toast("Server not running!")
                }
            } catch (e: java.net.SocketTimeoutException) {
                activity?.runOnUiThread {
                    binding.textDiagnosis.text = "⏰ CONNECTION TIMEOUT\n\n🚫 FIREWALL BLOCKING!\n\nError: ${e.message}\n\n📋 TO FIX:\n1. Disable Windows Firewall\n2. Check same WiFi network\n3. Try different IP"
                    requireContext().toast("Connection timeout - check firewall!")
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    binding.textDiagnosis.text = "❌ CONNECTION FAILED\n\nError: ${e.message}\nType: ${e.javaClass.simpleName}\n\n📋 POSSIBLE FIXES:\n1. Check Windows Firewall\n2. Verify same WiFi network\n3. Run: ipconfig\n4. Restart server"
                    requireContext().toast("Connection failed: ${e.javaClass.simpleName}")
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
