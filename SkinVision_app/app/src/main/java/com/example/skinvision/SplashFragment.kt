package com.example.skinvision

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAuthentication()
        }, 2000) // 2 seconds delay
    }

    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, go to home
            try {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } catch (e: Exception) {
                // Fallback to welcome if navigation fails
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            }
        } else {
            // No user is signed in, go to welcome
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }
}
