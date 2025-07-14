package com.example.skinvision

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skinvision.databinding.FragmentHelpCenterBinding

class HelpCenterFragment : Fragment() {

    private var _binding: FragmentHelpCenterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupSearch()
        setupFAQ()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSearch.setOnClickListener {
            binding.etSearch.requestFocus()
        }

        // Quick Action Cards
        binding.cardContactSupport.setOnClickListener {
            showContactSupportDialog()
        }

        binding.cardLiveChat.setOnClickListener {
            showLiveChatDialog()
        }

        binding.cardReportBug.setOnClickListener {
            showBugReportDialog()
        }

        // Contact Information
        binding.contactEmail.setOnClickListener {
            openEmailClient()
        }

        binding.contactPhone.setOnClickListener {
            dialPhoneNumber()
        }

        binding.contactWebsite.setOnClickListener {
            openWebsite()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
            }
        })
    }

    private fun performSearch(query: String) {
        val searchResults = when {
            query.contains("accuracy") || query.contains("ai") -> "Found: How accurate is the AI diagnosis?"
            query.contains("photo") || query.contains("camera") -> "Found: How to take better photos for analysis?"
            query.contains("privacy") || query.contains("secure") -> "Found: Is my data secure and private?"
            query.contains("contact") || query.contains("support") -> "Found: Contact Information section"
            else -> "No results found for '$query'"
        }

        requireContext().toast(searchResults)
    }

    private fun setupFAQ() {
        // FAQ 1
        binding.faqHeader1.setOnClickListener {
            toggleFAQ(binding.faqAnswer1, binding.faqArrow1)
        }

        // FAQ 2
        binding.faqHeader2.setOnClickListener {
            toggleFAQ(binding.faqAnswer2, binding.faqArrow2)
        }

        // FAQ 3
        binding.faqHeader3.setOnClickListener {
            toggleFAQ(binding.faqAnswer3, binding.faqArrow3)
        }
    }

    private fun toggleFAQ(answerView: View, arrowView: View) {
        if (answerView.visibility == View.GONE) {
            answerView.visibility = View.VISIBLE
            arrowView.animate().rotation(180f).setDuration(200).start()

            val slideDown = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
            answerView.startAnimation(slideDown)
        } else {
            answerView.visibility = View.GONE
            arrowView.animate().rotation(0f).setDuration(200).start()
        }
    }

    private fun showContactSupportDialog() {
        val options = arrayOf("Send Email", "Call Support", "Submit Ticket")

        AlertDialog.Builder(requireContext())
            .setTitle("Contact Support")
            .setMessage("How would you like to contact our support team?")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openEmailClient()
                    1 -> dialPhoneNumber()
                    2 -> showTicketDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLiveChatDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Live Chat")
            .setMessage("🤖 SkinVision Support Bot\n\nHello! I'm here to help you with:\n\n• App usage questions\n• Technical issues\n• Account problems\n• General inquiries\n\nOur live chat is available 24/7!")
            .setPositiveButton("Start Chat") { _, _ ->
                requireContext().toast("Connecting to support chat...")
                // Here you would integrate with a real chat service
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBugReportDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bug_report, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Report a Bug")
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                requireContext().toast("Bug report submitted! Thank you for helping us improve.")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTicketDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_support_ticket, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Submit Support Ticket")
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                requireContext().toast("Support ticket submitted! We'll respond within 24 hours.")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openEmailClient() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@skinvision.com")
            putExtra(Intent.EXTRA_SUBJECT, "SkinVision Support Request")
            putExtra(Intent.EXTRA_TEXT, "Hello SkinVision Support Team,\n\nI need help with:\n\n")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("No email app found. Please email: support@skinvision.com")
        }
    }

    private fun dialPhoneNumber() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:+18007546435") // +1-800-SKIN-HELP
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("No phone app found. Please call: +1-800-SKIN-HELP")
        }
    }

    private fun openWebsite() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.skinvision.com")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("No browser found. Please visit: www.skinvision.com")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
