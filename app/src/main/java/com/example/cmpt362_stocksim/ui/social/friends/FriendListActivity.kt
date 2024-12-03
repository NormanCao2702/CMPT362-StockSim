package com.example.cmpt362_stocksim.ui.social.friends

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.ui.social.chat.ChatActivity
import com.example.cmpt362_stocksim.ui.social.profile.ProfileActivity
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

/**
 * Activity for managing the user's friend list, including viewing friends, handling friend requests,
 * and searching for other users.
 */
class FriendListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    // UI components for search, friend list, and friend requests
    private lateinit var searchBar: SearchView
    private lateinit var searchList: ListView
    private lateinit var friendsList: ListView
    private lateinit var friendRequestList: ListView
    private lateinit var backButtonFriendListActivity: AppCompatImageButton

    // Adapters for handling list data
    private lateinit var friendSearchAdapter: FriendSearchAdapter
    private lateinit var friendsAdapter: FriendAdapter
    private lateinit var friendRequestAdapter: FriendRequestAdapter

    // Utility to manage user data and backend interaction
    private val userDataManager by lazy { UserDataManager(this) }
    private lateinit var backendViewModel: BackendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        // Initialize the ViewModel for backend interactions
        backendViewModel =
            BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)

        // Initialize UI components
        searchBar = findViewById(R.id.userSearch)
        searchList = findViewById(R.id.userSearchListview)
        friendsList = findViewById(R.id.friendListView)
        friendRequestList = findViewById(R.id.friendRequestListView)
        backButtonFriendListActivity = findViewById(R.id.backButtonFriendListActivity)

        backButtonFriendListActivity.setOnClickListener {
            finish()
        }

        // Load friends and friend requests
        updateFriends()
        updateFriendRequests()

        // Initialize adapters with empty data
        friendSearchAdapter = FriendSearchAdapter(this, ArrayList())
        friendsAdapter = FriendAdapter(this, ArrayList())
        friendRequestAdapter = FriendRequestAdapter(this, ArrayList()) {uid, accept ->
            // Handle accept/decline actions for friend requests
            lifecycleScope.launch {
                val token = userDataManager.getJwtToken()!!
                if(accept) {
                    backendViewModel.friendRequestAccept(uid.toString(), token)
                    updateFriends() // Refresh the friends list after accepting a request
                } else {
                    backendViewModel.friendRequestDecline(uid.toString(), token)
                }
                Log.d("MJR", uid.toString())
                updateFriendRequests()
            }
        }

        // Set listeners and adapters for UI components
        friendsList.setOnItemClickListener { adapterView, view, index, l ->
            val item = adapterView.getItemAtPosition(index) as BackendRepository.friend
            val user_id = item.uid
            val username = item.username
            // Open ChatActivity with the selected friend's details
            val args = Bundle()
            val intent = Intent(this, ChatActivity::class.java)
            args.putInt("USER_ID", user_id)
            args.putString("USERNAME", username)
            intent.putExtras(args)
            startActivity(intent)
        }

        searchList.adapter = friendSearchAdapter
        friendsList.adapter = friendsAdapter
        friendRequestList.adapter = friendRequestAdapter

        // Manage search bar focus behavior
        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchList.visibility = View.VISIBLE
            } else {
                searchList.visibility = View.INVISIBLE
            }
        }

        // Open ProfileActivity when clicking on a user in the search results
        searchBar.setOnQueryTextListener(this)
        searchList.setOnItemClickListener { adapterView, view, index, l ->
            val item = adapterView.getItemAtPosition(index) as BackendRepository.user
            val user_id = item.uid
            val args = Bundle()
            val intent = Intent(this, ProfileActivity::class.java)
            args.putInt("USER_ID", user_id)
            intent.putExtras(args)
            startActivity(intent)
        }
        setTitle("Friends")

    }

    /**
     * Fetches the user's current friends from the backend and updates the UI.
     */
    fun updateFriends() {
        lifecycleScope.launch {
            val uid = userDataManager.getUserId()!!
            val friends = backendViewModel.getFriends(uid)?.friends
            runOnUiThread {
                friendsAdapter.replace(friends!!)
                friendsAdapter.notifyDataSetInvalidated()
                friendsList.invalidateViews()
            }
        }
    }

    /**
     * Fetches friend requests from the backend and updates the UI.
     */
    fun updateFriendRequests() {
        lifecycleScope.launch {
            val token = userDataManager.getJwtToken()!!
            val friends = backendViewModel.getReceived(token)?.requests
            runOnUiThread {
                friendRequestAdapter.replace(friends!!)
                friendRequestAdapter.notifyDataSetInvalidated()
                friendRequestList.invalidateViews()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    /**
     * Handles live search as the user types in the search bar.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        lifecycleScope.launch {
            if (newText != null) {
                if(newText.isEmpty()) {
                    // Clear search results if the query is empty
                    Handler(Looper.getMainLooper()).post {
                        friendSearchAdapter.replace(ArrayList())
                        friendSearchAdapter.notifyDataSetInvalidated()
                        searchList.invalidateViews()
                    }
                } else {
                    var users: ArrayList<BackendRepository.user>? = null
                    try {
                        users = backendViewModel.getUsers(newText)?.users
                    } catch(e: IllegalArgumentException) {
                        Handler(Looper.getMainLooper()).post {
                            // Update search results with new data
                            friendSearchAdapter.replace(ArrayList())
                            friendSearchAdapter.notifyDataSetInvalidated()
                            searchList.invalidateViews()
                        }
                    }
                    if (users != null) {
                        Handler(Looper.getMainLooper()).post {
                            friendSearchAdapter.replace(users)
                            friendSearchAdapter.notifyDataSetInvalidated()
                            searchList.invalidateViews()
                        }
                    }
                }
            }
        }
        return false
    }
}