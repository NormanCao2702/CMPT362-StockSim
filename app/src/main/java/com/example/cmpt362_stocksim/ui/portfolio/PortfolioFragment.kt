package com.example.cmpt362_stocksim.ui.portfolio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_stocksim.databinding.FragmentPortfolioBinding
import com.example.cmpt362_stocksim.ui.auth.LoginActivity

class PortfolioFragment: Fragment() {

    private var _binding: FragmentPortfolioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val portfolioViewModel=
            ViewModelProvider(this).get(PortfolioViewModel::class.java)

        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPortfolio
        portfolioViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        return root
    }

    private fun logout() {
        // Clear JWT token
        requireActivity().getSharedPreferences("AUTH", Context.MODE_PRIVATE)
            .edit()
            .remove("JWT_TOKEN")
            .apply()

        // Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}