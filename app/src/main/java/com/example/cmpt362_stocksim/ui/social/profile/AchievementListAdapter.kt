package com.example.cmpt362_stocksim.ui.social.profile

import android.app.Activity
import android.icu.util.Calendar
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.cmpt362_stocksim.BackendRepository
import java.text.ParseException
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AchievementListAdapter(private val activity: Activity,
                             private var feedList: List<BackendRepository.achieves>): BaseAdapter() {
    override fun getCount(): Int {
        return feedList.size
    }

    override fun getItem(index: Int): BackendRepository.achieves {
        return feedList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return feedList.get(index).id.toLong()
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, android.R.layout.simple_list_item_2, null)
        val item = getItem(id)
        val achievement = item.name
        val description = item.description
        newView.findViewById<TextView>(android.R.id.text1).text = achievement + " - " + getTimeAgo(item.date)
        newView.findViewById<TextView>(android.R.id.text2).text = description
        return newView
    }

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

    fun replace(list: List<BackendRepository.achieves>) {
        feedList = list
    }
}