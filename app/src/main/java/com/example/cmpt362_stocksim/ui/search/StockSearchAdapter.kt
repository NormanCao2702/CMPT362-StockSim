package com.example.cmpt362_stocksim.ui.search

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.api.BackendRepository

/**
 * This adapter is for creating the list of stocks that pop up when you type in ticker symbols in the search page
 */
class StockSearchAdapter(private val context: Context, private var stockList: List<BackendRepository.stock>): BaseAdapter() {

    // Returns the size of the stockList
    override fun getCount(): Int {
        return stockList.size
    }

    // Returns the stock at a specific index
    override fun getItem(index: Int): BackendRepository.stock {
        return stockList.get(index)
    }

    // Unused function, required by BaseAdapter
    override fun getItemId(index: Int): Long {
        return 43543
    }

    // This retrieves the view and sets each listitem in the search list with its stocks symbol and price
    override fun getView(id: Int, view: View?, viewGroup: ViewGroup?): View {
        val newView = View.inflate(context, R.layout.stock_search_item, null)
        val item = getItem(id)
        newView.findViewById<TextView>(R.id.user_list_username).text = "  ${item.symbol}          Price: $${item.price}"
        return newView
    }

    // Replaces the current list with a new list
    fun replace(list: List<BackendRepository.stock>) {
        stockList = list
    }
}