package com.example.cmpt362_stocksim.ui.social

import android.app.Activity
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.cmpt362_stocksim.api.BackendRepository
import java.text.ParseException
import java.time.OffsetDateTime
import java.time.ZoneOffset

// Adapter for displaying the feed items in a list view
class FeedArrayListAdapter(private val activity: Activity,
                           private var feedList: List<BackendRepository.feedItem>): BaseAdapter() {

    // Return the number of items in the feed
     override fun getCount(): Int {
        return feedList.size
     }

    // Get a specific item at the provided index
    override fun getItem(index: Int): BackendRepository.feedItem {
        return feedList.get(index)
    }

    // Return the ID of the item (post_id in this case)
    override fun getItemId(index: Int): Long {
        return feedList.get(index).post_id.toLong()
    }

    // Bind the data to the view (layout for each feed item)
    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, android.R.layout.simple_list_item_2, null)
        val item = getItem(id)
        val user = item.username
        val content = item.content
        newView.findViewById<TextView>(android.R.id.text1).text = user + " - " + getTimeAgo(item.date)
        newView.findViewById<TextView>(android.R.id.text2).text = content
        return newView
    }

    // Method to convert the date into a "time ago" format (e.g., "5 minutes ago")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(date: Long): String {
        try {
            val cal = Calendar.getInstance()

            cal.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
            val now = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()
            val given = date
            val ago =
                DateUtils.getRelativeTimeSpanString(given * 1000, now * 1000, DateUtils.SECOND_IN_MILLIS).toString()
            return ago
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    // Replace the current feed list with a new list
    fun replace(list: List<BackendRepository.feedItem>) {
        feedList = list
    }
}