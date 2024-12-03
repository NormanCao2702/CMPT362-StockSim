package com.example.cmpt362_stocksim.ui.social.friends

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.R

/**
 * Adapter class to handle the display of a list of friends in a custom ListView.
 */
class FriendAdapter(private val activity: Activity,
                    private var userList: List<BackendRepository.friend>): BaseAdapter() {
     //Returns the total number of items in the friend list
    override fun getCount(): Int {
        return userList.size
    }
    //Returns the friend object at the specified index.
    override fun getItem(index: Int): BackendRepository.friend {
        return userList.get(index)
    }
    // Returns a unique ID for the item at the specified position.
    override fun getItemId(index: Int): Long {
        return getItem(index).uid.toLong()
    }

    //Inflates the view for each list item and populates it with the friend's data.
    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, R.layout.friend_list_search_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.user_list_username).text = item.username
        return newView
    }
    //Replaces the current list of friends with a new list and notifies the adapter to refresh the view.
    fun replace(list: List<BackendRepository.friend>) {
        userList = list
    }
}