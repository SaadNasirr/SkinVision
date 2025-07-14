package com.example.skinvision

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skinvision.adapters.DoctorsAdapter
import com.example.skinvision.data.Doctor
import com.example.skinvision.databinding.FragmentDoctorsBinding

class DoctorsFragment : Fragment() {

    private var _binding: FragmentDoctorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var doctorsAdapter: DoctorsAdapter
    private var allDoctors = listOf<Doctor>()
    private var filteredDoctors = listOf<Doctor>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        loadDoctors()
    }

    private fun setupRecyclerView() {
        doctorsAdapter = DoctorsAdapter(emptyList()) { doctor ->
            showDoctorProfile(doctor)
        }

        binding.recyclerViewDoctors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = doctorsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.btnLocation.setOnClickListener {
            requireContext().toast("Getting your location...")
        }

        // Filter chips
        binding.chipAll.setOnClickListener { filterDoctors("all") }
        binding.chipDermatology.setOnClickListener { filterDoctors("dermatology") }
        binding.chipCosmetic.setOnClickListener { filterDoctors("cosmetic") }
        binding.chipPediatric.setOnClickListener { filterDoctors("pediatric") }
        binding.chipNearby.setOnClickListener { filterDoctors("nearby") }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchDoctors(s.toString())
            }
        })
    }

    private fun loadDoctors() {
        // Sample data - in real app, this would come from API
        allDoctors = listOf(
            Doctor(
                id = "1",
                name = "Dr. Sarah Johnson",
                specialty = "Dermatologist",
                rating = 4.8,
                reviewCount = 124,
                location = "City Hospital, Downtown",
                distance = "2.5 km",
                experience = "15+ years experience",
                isAvailable = true,
                phone = "+1234567890",
                consultationFee = "$150",
                languages = listOf("English", "Spanish"),
                education = "Harvard Medical School",
                about = "Specialized in skin cancer detection and cosmetic dermatology."
            ),
            Doctor(
                id = "2",
                name = "Dr. Michael Chen",
                specialty = "Pediatric Dermatologist",
                rating = 4.9,
                reviewCount = 89,
                location = "Children's Medical Center",
                distance = "3.2 km",
                experience = "12+ years experience",
                isAvailable = false,
                phone = "+1234567891",
                consultationFee = "$120",
                languages = listOf("English", "Mandarin"),
                education = "Johns Hopkins University",
                about = "Expert in treating skin conditions in children and adolescents."
            ),
            Doctor(
                id = "3",
                name = "Dr. Emily Rodriguez",
                specialty = "Cosmetic Dermatologist",
                rating = 4.7,
                reviewCount = 156,
                location = "Beauty & Wellness Clinic",
                distance = "1.8 km",
                experience = "10+ years experience",
                isAvailable = true,
                phone = "+1234567892",
                consultationFee = "$200",
                languages = listOf("English", "Spanish", "French"),
                education = "Stanford Medical School",
                about = "Specializes in anti-aging treatments and cosmetic procedures."
            ),
            Doctor(
                id = "4",
                name = "Dr. James Wilson",
                specialty = "Dermatologist",
                rating = 4.6,
                reviewCount = 203,
                location = "Metro General Hospital",
                distance = "4.1 km",
                experience = "20+ years experience",
                isAvailable = true,
                phone = "+1234567893",
                consultationFee = "$180",
                languages = listOf("English"),
                education = "Yale School of Medicine",
                about = "Experienced in treating complex skin disorders and autoimmune conditions."
            )
        )

        filteredDoctors = allDoctors
        doctorsAdapter.updateDoctors(filteredDoctors)
    }

    private fun searchDoctors(query: String) {
        filteredDoctors = if (query.isEmpty()) {
            allDoctors
        } else {
            allDoctors.filter { doctor ->
                doctor.name.contains(query, ignoreCase = true) ||
                        doctor.specialty.contains(query, ignoreCase = true) ||
                        doctor.location.contains(query, ignoreCase = true)
            }
        }
        doctorsAdapter.updateDoctors(filteredDoctors)
    }

    private fun filterDoctors(filter: String) {
        // Reset chip colors
        resetChipColors()

        filteredDoctors = when (filter) {
            "all" -> {
                binding.chipAll.setChipBackgroundColorResource(R.color.colorPrimary)
                allDoctors
            }
            "dermatology" -> {
                binding.chipDermatology.setChipBackgroundColorResource(R.color.colorPrimary)
                allDoctors.filter { it.specialty.contains("Dermatologist", ignoreCase = true) }
            }
            "cosmetic" -> {
                binding.chipCosmetic.setChipBackgroundColorResource(R.color.colorPrimary)
                allDoctors.filter { it.specialty.contains("Cosmetic", ignoreCase = true) }
            }
            "pediatric" -> {
                binding.chipPediatric.setChipBackgroundColorResource(R.color.colorPrimary)
                allDoctors.filter { it.specialty.contains("Pediatric", ignoreCase = true) }
            }
            "nearby" -> {
                binding.chipNearby.setChipBackgroundColorResource(R.color.colorPrimary)
                allDoctors.sortedBy { it.distance.replace(" km", "").toDoubleOrNull() ?: 999.0 }
            }
            else -> allDoctors
        }

        doctorsAdapter.updateDoctors(filteredDoctors)
    }

    private fun resetChipColors() {
        binding.chipAll.setChipBackgroundColorResource(android.R.color.transparent)
        binding.chipDermatology.setChipBackgroundColorResource(android.R.color.transparent)
        binding.chipCosmetic.setChipBackgroundColorResource(android.R.color.transparent)
        binding.chipPediatric.setChipBackgroundColorResource(android.R.color.transparent)
        binding.chipNearby.setChipBackgroundColorResource(android.R.color.transparent)
    }

    private fun showFilterDialog() {
        val options = arrayOf("Rating: High to Low", "Distance: Near to Far", "Experience: Most to Least", "Availability: Available First")

        AlertDialog.Builder(requireContext())
            .setTitle("Sort & Filter")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sortByRating()
                    1 -> sortByDistance()
                    2 -> sortByExperience()
                    3 -> sortByAvailability()
                }
            }
            .show()
    }

    private fun sortByRating() {
        filteredDoctors = filteredDoctors.sortedByDescending { it.rating }
        doctorsAdapter.updateDoctors(filteredDoctors)
        requireContext().toast("Sorted by rating")
    }

    private fun sortByDistance() {
        filteredDoctors = filteredDoctors.sortedBy { it.distance.replace(" km", "").toDoubleOrNull() ?: 999.0 }
        doctorsAdapter.updateDoctors(filteredDoctors)
        requireContext().toast("Sorted by distance")
    }

    private fun sortByExperience() {
        filteredDoctors = filteredDoctors.sortedByDescending {
            it.experience.replace("+", "").replace(" years experience", "").toIntOrNull() ?: 0
        }
        doctorsAdapter.updateDoctors(filteredDoctors)
        requireContext().toast("Sorted by experience")
    }

    private fun sortByAvailability() {
        filteredDoctors = filteredDoctors.sortedByDescending { it.isAvailable }
        doctorsAdapter.updateDoctors(filteredDoctors)
        requireContext().toast("Available doctors first")
    }

    private fun showDoctorProfile(doctor: Doctor) {
        val message = """
            👨‍⚕️ ${doctor.name}
            
            🏥 Specialty: ${doctor.specialty}
            ⭐ Rating: ${doctor.rating}/5.0 (${doctor.reviewCount} reviews)
            📍 Location: ${doctor.location}
            📏 Distance: ${doctor.distance}
            🎓 Experience: ${doctor.experience}
            💰 Consultation: ${doctor.consultationFee}
            🗣️ Languages: ${doctor.languages.joinToString(", ")}
            
            📚 Education: ${doctor.education}
            
            ℹ️ About: ${doctor.about}
            
            ${if (doctor.isAvailable) "✅ Available for appointment today" else "⏰ Next available: Tomorrow"}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Doctor Profile")
            .setMessage(message)
            .setPositiveButton("Book Appointment") { _, _ ->
                requireContext().toast("Booking appointment with ${doctor.name}")
            }
            .setNeutralButton("Call Now") { _, _ ->
                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                    data = android.net.Uri.parse("tel:${doctor.phone}")
                }
                startActivity(intent)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
