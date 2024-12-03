package com.example.cmpt362_stocksim.ui.social.friends

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.R

class FriendAdapter(private val activity: Activity,
                    private var userList: List<BackendRepository.friend>): BaseAdapter() {
    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(index: Int): BackendRepository.friend {
        return userList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index).uid.toLong()
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, R.layout.friend_list_search_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.user_list_username).text = item.username
        return newView
    }

    fun replace(list: List<BackendRepository.friend>) {
        userList = list
    }
}