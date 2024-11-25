package com.example.cmpt362_stocksim.ui.social

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.R

class FeedArrayListAdapter(private val activity: Activity,
                           private var feedList: List<BackendRepository.feedItem>): BaseAdapter() {
    override fun getCount(): Int {
        return feedList.size
    }

    override fun getItem(index: Int): BackendRepository.feedItem {
        return feedList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return feedList.get(index).post_id.toLong()
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, android.R.layout.simple_list_item_2, null)
        val item = getItem(id)
        val user = item.username
        val content = item.content
        newView.findViewById<TextView>(android.R.id.text1).text = user
        newView.findViewById<TextView>(android.R.id.text2).text = content
        return newView
    }

    fun replace(list: List<BackendRepository.feedItem>) {
        feedList = list
    }
}