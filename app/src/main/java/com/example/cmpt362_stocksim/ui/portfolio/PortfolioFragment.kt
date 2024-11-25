package com.example.cmpt362_stocksim.ui.portfolio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.databinding.FragmentPortfolioBinding
import com.example.cmpt362_stocksim.ui.auth.LoginActivity
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

class PortfolioFragment: Fragment() {

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!
    private lateinit var portfolioViewModel: PortfolioViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImage.launch("image/*")
        } else {
            Toast.makeText(context, "Permission required to select image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndPickImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                pickImage.launch("image/*") // No permission needed for Android 13+
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImage.launch("image/*")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        portfolioViewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)

        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        setupUI()
        loadUserData()
        loadProfileImage()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        // Observe stats data
        portfolioViewModel.statsData.observe(viewLifecycleOwner) { stats ->
            binding.apply {
                tvAchievementsCount.text = stats.achievementCount.toString()
                tvStocksCount.text = stats.stocksCount.toString()
                tvFavoritesCount.text = stats.favoritesCount.toString()
            }
        }
    }

    // This will be called every time the fragment becomes visible
    override fun onResume() {
        super.onResume()
        refreshUserData()
    }

    private fun refreshUserData() {
        val userId = UserDataManager(requireContext()).getUserId()
        userId?.let {
            lifecycleScope.launch {
                try {
                    // Refresh basic user info
                    UserDataManager(requireContext()).refreshUserInfo()

                    // Refresh stats (achievements, stocks, favorites)
                    portfolioViewModel.loadUserStats(it, BackendRepository())

                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to refresh data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, selectedImageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                }

                // Save the image
                UserDataManager(requireContext()).saveProfileImage(bitmap)

                // Display the image
                binding.profileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe stats data
        portfolioViewModel.statsData.observe(viewLifecycleOwner) { stats ->
            binding.tvAchievementsCount.text = stats.achievementCount.toString()
            binding.tvStocksCount.text = stats.stocksCount.toString()
            binding.tvFavoritesCount.text = stats.favoritesCount.toString()
        }

        // Load stats
        val userId = UserDataManager(requireContext()).getUserId()
        userId?.let {
            lifecycleScope.launch {
                portfolioViewModel.loadUserStats(it, BackendRepository())
            }
        }
    }

    private fun loadProfileImage() {
        val userData = UserDataManager(requireContext())
        val savedImage = userData.getProfileImage()

        if (savedImage != null) {
            binding.profileImage.setImageBitmap(savedImage)
        } else {
            // Set default profile image
            binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun setupUI() {
        // Setup logout button
        binding.btnLogout.setOnClickListener {
            logout()
        }

        // Update profile image click to use permission check
        binding.profileImage.setOnClickListener {
            checkPermissionAndPickImage()
        }
    }

    private fun loadUserData() {
        // Initial load from stored data
        val userData = UserDataManager(requireContext())
        binding.apply {
            tvUsername.text = userData.getUsername() ?: "Username"
            tvEmail.text = userData.getEmail() ?: "email@example.com"
            tvBirthday.text = userData.getBirthday() ?: "YYYY-MM-DD"
            tvNetWorth.text = formatCurrency(userData.getNetWorth())
        }

        // Refresh data from API
        lifecycleScope.launch {
            try {
                if (userData.refreshUserInfo()) {
                    // Update UI with fresh data
                    binding.apply {
                        tvUsername.text = userData.getUsername()
                        tvEmail.text = userData.getEmail()
                        tvBirthday.text = userData.getBirthday()
                        tvNetWorth.text = formatCurrency(userData.getNetWorth())
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to refresh user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        return "$${String.format("%.2f", amount)}"
    }

    private fun logout() {
        // Clear user data
        UserDataManager(requireContext()).clearUserData()

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