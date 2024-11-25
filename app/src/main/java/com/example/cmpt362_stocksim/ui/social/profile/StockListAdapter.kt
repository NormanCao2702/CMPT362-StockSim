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

class StockListAdapter(private val activity: Activity,
                       private var stockList: List<BackendRepository.stockInv>): BaseAdapter() {
    override fun getCount(): Int {
        return stockList.size
    }

    override fun getItem(index: Int): BackendRepository.stockInv {
        return stockList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(activity, android.R.layout.simple_list_item_1, null)
        val item = getItem(id)
        newView.findViewById<TextView>(android.R.id.text1).text = item.symbol + " - " + item.amount.toString()
        return newView
    }

    fun replace(list: List<BackendRepository.stockInv>) {
        stockList = list
    }
}