package com.example.cmpt362_stocksim.ui.social

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.ui.social.friends.FriendListActivity
import com.example.cmpt362_stocksim.databinding.FragmentSocialBinding
import com.example.cmpt362_stocksim.ui.social.profile.ProfileActivity
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

/**
 * This class is the Global feed in the social tab
 */
class SocialFragment: Fragment() {

    // Initialize backendviewmodel
    private lateinit var backendViewModel: BackendViewModel
    private var _binding: FragmentSocialBinding? = null

    // Initialize UI elements
    private lateinit var postTextBox: EditText
    private lateinit var feedListView: ListView
    private lateinit var backButtonSocialFragment: AppCompatImageButton

    // Initialize feed adapter
    private lateinit var feedAdapter: FeedArrayListAdapter

    // Initialize data manager
    private val userDataManager by lazy { UserDataManager(requireActivity()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout using the binding object
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize views from the layout (TextBox and ListView)
        postTextBox = binding.postTextbox
        feedListView = binding.feedListview

        // Set up listener for the 'friend list' button
        binding.friendListButton.setOnClickListener{
            //friend list button click event
            val intent = Intent(requireContext(), FriendListActivity::class.java)
            startActivity(intent)
        }


        // Initialize ViewModel and adapter for the feed

        backendViewModel = BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)
        feedAdapter = FeedArrayListAdapter(requireActivity(), ArrayList())
        feedListView.adapter = feedAdapter

        // Set up an item click listener for the feed list
        feedListView.setOnItemClickListener { adapterView, view, index, l ->
            val item = adapterView.getItemAtPosition(index) as BackendRepository.feedItem
            val user_id = item.uid
            val args = Bundle()
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            args.putInt("USER_ID", user_id)
            intent.putExtras(args)
            startActivity(intent)
        }
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Set up listener for the 'create post' button
        binding.buttonCreatePost.setOnClickListener{
            //create post button click event
            lifecycleScope.launch {
                val token = userDataManager.getJwtToken()!!
                try {
                    backendViewModel.setPost(postTextBox.text.toString(), token)
                    requireActivity().runOnUiThread {
                        postTextBox.setText("")
                        Toast.makeText(requireActivity(), "Post created!", Toast.LENGTH_LONG).show()
                    }
                    getNewFeed()
                }catch(e: IllegalArgumentException) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        return root
    }

    // Function to fetch the updated feed
    fun getNewFeed() {
        lifecycleScope.launch {
            val feedItems = backendViewModel.getFeed()
            val runnable = Runnable {
                feedAdapter.replace(feedItems)
                feedAdapter.notifyDataSetChanged()
                feedListView.invalidateViews()
                feedListView.setSelectionAfterHeaderView()
            }
            Handler(Looper.getMainLooper()).post(runnable)
        }
    }

    // Refresh the feed
    override fun onResume() {
        super.onResume()
        getNewFeed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}