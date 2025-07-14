package com.example.skinvision.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skinvision.R
import com.example.skinvision.data.Doctor
import com.example.skinvision.databinding.ItemDoctorBinding
import com.example.skinvision.toast

class DoctorsAdapter(
    private var doctors: List<Doctor>,
    private val onDoctorClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val binding = ItemDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]

        with(holder.binding) {
            tvDoctorName.text = doctor.name
            tvSpecialty.text = doctor.specialty
            tvRating.text = doctor.rating.toString()
            tvReviews.text = "(${doctor.reviewCount} reviews)"
            tvLocation.text = doctor.location
            tvDistance.text = doctor.distance
            tvExperience.text = doctor.experience

            // Set availability
            if (doctor.isAvailable) {
                tvAvailability.text = "Available Today"
                tvAvailability.setBackgroundResource(R.drawable.availability_badge)
            } else {
                tvAvailability.text = "Next: Tomorrow"
                tvAvailability.setBackgroundResource(R.drawable.availability_badge_unavailable)
            }

            // Set doctor image placeholder
            ivDoctorImage.setImageResource(
                when (position % 4) {
                    0 -> R.drawable.doctor_1
                    1 -> R.drawable.doctor_2
                    2 -> R.drawable.doctor_3
                    else -> R.drawable.doctor_4
                }
            )

            // Click listeners
            btnCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${doctor.phone}")
                }
                holder.itemView.context.startActivity(intent)
            }

            btnBook.setOnClickListener {
                holder.itemView.context.toast("Booking appointment with ${doctor.name}")
                // Here you would integrate with a real booking system
            }

            btnProfile.setOnClickListener {
                onDoctorClick(doctor)
            }

            btnFavorite.setOnClickListener {
                holder.itemView.context.toast("Added ${doctor.name} to favorites")
                // Toggle favorite state
            }

            root.setOnClickListener {
                onDoctorClick(doctor)
            }
        }
    }

    override fun getItemCount(): Int = doctors.size

    fun updateDoctors(newDoctors: List<Doctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }
}
