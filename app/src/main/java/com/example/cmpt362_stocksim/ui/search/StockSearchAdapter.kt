package com.example.cmpt362_stocksim.ui.search


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.api.BackendRepository

class StockSearchAdapter(private val context: Context, private var stockList: List<BackendRepository.stock>): BaseAdapter() {

    override fun getCount(): Int {
        return stockList.size
    }

    override fun getItem(index: Int): BackendRepository.stock {
        return stockList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return 43543
    }

    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(context, R.layout.stock_search_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.user_list_username).text = "  ${item.symbol},   Price: ${item.price}"
        return newView
    }

    fun replace(list: List<BackendRepository.stock>) {
        stockList = list
    }
}