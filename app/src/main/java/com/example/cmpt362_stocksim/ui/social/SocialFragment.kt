package com.example.cmpt362_stocksim.ui.social

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.BackendViewModel
import com.example.cmpt362_stocksim.BackendViewModelFactory
import com.example.cmpt362_stocksim.FriendListActivity
import com.example.cmpt362_stocksim.databinding.FragmentSocialBinding
import kotlinx.coroutines.launch

class SocialFragment: Fragment() {
    private lateinit var backendViewModel: BackendViewModel

    private var _binding: FragmentSocialBinding? = null
    private lateinit var postTextBox: EditText
    private lateinit var feedListView: ListView

    private lateinit var feedAdapter: FeedArrayListAdapter


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val socialViewModel =
            ViewModelProvider(this).get(SocialViewModel::class.java)

        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        postTextBox = binding.postTextbox
        feedListView = binding.feedListview

        binding.friendListButton.setOnClickListener{
            //friend list button click event

            val intent = Intent(requireContext(), FriendListActivity::class.java)
            startActivity(intent)
        }

        binding.buttonCreatePost.setOnClickListener{
            //create post button click event
        }


        backendViewModel = BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)
        feedAdapter = FeedArrayListAdapter(requireActivity(), ArrayList())
        feedListView.adapter = feedAdapter

//        val textView: TextView = binding.textSocial
//        socialViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    fun getNewFeed() {
        lifecycleScope.launch {
            val feedItems = backendViewModel.getFeed()
            val runnable = Runnable {
                feedAdapter.replace(feedItems)
                feedAdapter.notifyDataSetChanged()
                feedListView.invalidateViews()
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