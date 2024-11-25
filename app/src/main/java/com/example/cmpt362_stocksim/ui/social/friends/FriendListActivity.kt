package com.example.cmpt362_stocksim.ui.social.friends

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.BackendViewModel
import com.example.cmpt362_stocksim.BackendViewModelFactory
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.ui.social.chat.ChatActivity
import com.example.cmpt362_stocksim.ui.social.profile.ProfileActivity
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

class FriendListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var searchBar: SearchView
    private lateinit var searchList: ListView
    private lateinit var friendsList: ListView

    private lateinit var friendRequestList: ListView

    private lateinit var friendSearchAdapter: FriendSearchAdapter
    private lateinit var friendsAdapter: FriendAdapter
    private lateinit var friendRequestAdapter: FriendRequestAdapter

    private val userDataManager by lazy { UserDataManager(this) }
    private lateinit var backendViewModel: BackendViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)
        backendViewModel =
            BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)

        searchBar = findViewById(R.id.userSearch)
        searchList = findViewById(R.id.userSearchListview)
        friendsList = findViewById(R.id.friendListView)
        friendRequestList = findViewById(R.id.friendRequestListView)

        updateFriends()
        updateFriendRequests()

        friendSearchAdapter = FriendSearchAdapter(this, ArrayList())
        friendsAdapter = FriendAdapter(this, ArrayList())
        friendRequestAdapter = FriendRequestAdapter(this, ArrayList()) {uid, accept ->
            lifecycleScope.launch {
                val token = userDataManager.getJwtToken()!!
                if(accept) {
                    backendViewModel.friendRequestAccept(uid.toString(), token)
                    updateFriends()
                } else {
                    backendViewModel.friendRequestDecline(uid.toString(), token)
                }
                Log.d("MJR", uid.toString())
                updateFriendRequests()
            }
        }

        friendsList.setOnItemClickListener { adapterView, view, index, l ->
            val item = adapterView.getItemAtPosition(index) as BackendRepository.friend
            val user_id = item.uid
            val username = item.username
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

        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchList.visibility = View.VISIBLE
            } else {
                searchList.visibility = View.INVISIBLE
            }
        }

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

    override fun onQueryTextChange(newText: String?): Boolean {
        lifecycleScope.launch {
            if (newText != null) {
                if(newText.isEmpty()) {
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