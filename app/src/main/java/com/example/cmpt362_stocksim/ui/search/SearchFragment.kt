package com.example.cmpt362_stocksim.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

/**
 * This fragment is for stock searching,  the search icon (second from left) in the bottom nav bar
 */
class SearchFragment: Fragment(), SearchView.OnQueryTextListener  {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // Initialize UI elements
    private lateinit var searchBar: SearchView
    private lateinit var searchList: ListView
    private lateinit var clickedList: ListView

    // Initialize adapter for listviews
    private lateinit var stockSearchAdapter: StockSearchAdapter
    private lateinit var clickedListAdapter: StockSearchAdapter

    // Initialize arraylist for the recently clicked listview
    private val clickedItems = ArrayList<BackendRepository.stock>()

    // Initialize backend repository that connects our backend to our frontend
    val repository = BackendRepository()
    val viewModelFactory = BackendViewModelFactory(repository)
    val backendViewModel = viewModelFactory.create(BackendViewModel::class.java)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout using data binding
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val root: View = binding.root
        // Initialize UI elements
        searchBar = binding.stockSearch
        searchList = binding.stockSearchListview
        clickedList = binding.recentSearchListView

        // Set up adapters for search results and recently clicked lists
        stockSearchAdapter = StockSearchAdapter(requireContext(), ArrayList())
        searchList.adapter = stockSearchAdapter
        clickedListAdapter = StockSearchAdapter(requireContext(), clickedItems)
        clickedList.adapter = clickedListAdapter

        // Show or hide the search list based on the search bar's focus
        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchList.visibility = View.VISIBLE
            } else {
                searchList.visibility = View.INVISIBLE
            }
        }

        // Attach query listener to the search bar
        searchBar.setOnQueryTextListener(this)

        // Handle clicks on search result items
        searchList.setOnItemClickListener { adapterView, view, index, l ->

            val item = adapterView.getItemAtPosition(index) as BackendRepository.stock

            // Update recently clicked items list
            if (!clickedItems.contains(item)) {
                if (clickedItems.size >= 3) {
                    clickedItems.removeAt(0) // Remove the oldest item
                }
                clickedItems.add(item)
                clickedListAdapter.replace(clickedItems)
                clickedListAdapter.notifyDataSetChanged()
            }
            // Navigate to the detailed stock entry screen
            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
            intent.putExtra("tickerName", item.symbol)
            startActivity(intent)
        }
        // Handle clicks on recently clicked items
        clickedList.setOnItemClickListener { adapterView, view, index, id ->
            val clickedItem = adapterView.getItemAtPosition(index) as BackendRepository.stock
            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
            intent.putExtra("tickerName", clickedItem.symbol)
            startActivity(intent)
        }
        return root
    }


    /**
     * Handles text submission in the search bar.
     * Currently, it doesn't do anything
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    /**
     * Handles text changes in the search bar.
     * Updates the search results dynamically.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        lifecycleScope.launch {
            if (newText != null) {
                // Clear search results if the input is empty
                if(newText.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        stockSearchAdapter.replace(ArrayList())
                        stockSearchAdapter.notifyDataSetInvalidated()
                        searchList.invalidateViews()
                    }
                } else {
                    var stocks: ArrayList<BackendRepository.stock>? = null
                    try {
                        // Fetch matching stocks from the backend
                        stocks = backendViewModel.getStocks(newText)?.tickers
                    } catch(e: IllegalArgumentException) {
                        // Handle errors
                        Handler(Looper.getMainLooper()).post {
                            stockSearchAdapter.replace(ArrayList())
                            stockSearchAdapter.notifyDataSetInvalidated()
                            searchList.invalidateViews()
                        }
                    }
                    if (stocks != null) {
                        // Update the adapter with the fetched results
                        Handler(Looper.getMainLooper()).post {
                            stockSearchAdapter.replace(stocks)
                            stockSearchAdapter.notifyDataSetInvalidated()
                            searchList.invalidateViews()
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     *  Cleans up binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}