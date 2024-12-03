package com.example.cmpt362_stocksim.ui.social.friends

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.R

//Custom adapter for displaying a list of users in the friend search results.
class FriendSearchAdapter(private val activity: Activity,
                          private var userList: List<BackendRepository.user>): BaseAdapter() {
    //Returns the total number of items in the list
    override fun getCount(): Int {
        return userList.size
    }
    //Retrieves the item at the specified position in the list.
    override fun getItem(index: Int): BackendRepository.user {
        return userList.get(index)
    }
    //Returns the unique ID of the item at the specified position.
    override fun getItemId(index: Int): Long {
        return getItem(index).uid.toLong()
    }

    // Creates or updates the view in the list.
    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, R.layout.friend_list_search_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.user_list_username).text = item.username
        return newView
    }
    // Replaces the current list of users with a new list.
    fun replace(list: List<BackendRepository.user>) {
        userList = list
    }
}