package com.example.cmpt362_stocksim.ui.social.profile

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.api.BackendRepository

// Custom adapter for displaying a list of stock inventory items in a ListView.
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