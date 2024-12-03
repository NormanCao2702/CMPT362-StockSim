package com.example.cmpt362_stocksim.ui.social.friends

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.R

class FriendRequestAdapter(private val activity: Activity,
                           private var userList: List<BackendRepository.request>,
                           private val responseCallback: ((id: Int, accept: Boolean)->Unit)): BaseAdapter() {
    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(index: Int): BackendRepository.request {
        return userList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index).uid.toLong()
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, R.layout.friend_request_list_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.friendRequestUsernameTextView).text = item.username

        newView.findViewById<ImageButton>(R.id.friendRequestAcceptButton).setOnClickListener {
            responseCallback(item.uid, true)
        }

        newView.findViewById<ImageButton>(R.id.friendRequestCancelButton).setOnClickListener {
            responseCallback(item.uid, false)
        }

        return newView
    }

    fun replace(list: List<BackendRepository.request>) {
        userList = list
    }
}