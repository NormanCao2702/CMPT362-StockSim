package com.example.cmpt362_stocksim.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cmpt362_stocksim.databinding.FragmentSocialBinding

class SocialFragment: Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private lateinit var postTextBox: EditText


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

        binding.friendListButton.setOnClickListener{
            //friend list button click event
        }

        binding.buttonCreatePost.setOnClickListener{
            //create post button click event
        }

        binding.buttonSendPost.setOnClickListener{
            //send post button click event
        }

//        val textView: TextView = binding.textSocial
//        socialViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}