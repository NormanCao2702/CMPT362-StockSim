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


class SearchFragment: Fragment(), SearchView.OnQueryTextListener  {
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var searchBar: SearchView
    private lateinit var searchList: ListView
    private lateinit var clickedList: ListView
    private lateinit var stockSearchAdapter: StockSearchAdapter


    private val clickedItems = ArrayList<BackendRepository.stock>()
    private lateinit var clickedListAdapter: StockSearchAdapter

    val repository = BackendRepository()
    val viewModelFactory = BackendViewModelFactory(repository)
    val backendViewModel = viewModelFactory.create(BackendViewModel::class.java)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchBar = binding.stockSearch
        searchList = binding.stockSearchListview
        clickedList = binding.recentSearchListView

        stockSearchAdapter = StockSearchAdapter(requireContext(), ArrayList())
        searchList.adapter = stockSearchAdapter


        clickedListAdapter = StockSearchAdapter(requireContext(), clickedItems)
        clickedList.adapter = clickedListAdapter


        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchList.visibility = View.VISIBLE
            } else {
                searchList.visibility = View.INVISIBLE
            }
        }

        searchBar.setOnQueryTextListener(this)
        searchList.setOnItemClickListener { adapterView, view, index, l ->

            val item = adapterView.getItemAtPosition(index) as BackendRepository.stock
            if (!clickedItems.contains(item)) {
                if (clickedItems.size >= 3) {
                    clickedItems.removeAt(0) // Remove the oldest item
                }
                clickedItems.add(item)
                clickedListAdapter.replace(clickedItems)
                clickedListAdapter.notifyDataSetChanged()
            }

            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
            intent.putExtra("tickerName", item.symbol)
            startActivity(intent)


        }

        clickedList.setOnItemClickListener { adapterView, view, index, id ->
            val clickedItem = adapterView.getItemAtPosition(index) as BackendRepository.stock
            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
            intent.putExtra("tickerName", clickedItem.symbol)
            startActivity(intent)
        }





        return root
    }



    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        lifecycleScope.launch {
            if (newText != null) {
                if(newText.isEmpty()) {
                    Handler(Looper.getMainLooper()).post {
                        stockSearchAdapter.replace(ArrayList())
                        stockSearchAdapter.notifyDataSetInvalidated()
                        searchList.invalidateViews()
                    }
                } else {
                    var stocks: ArrayList<BackendRepository.stock>? = null
                    try {
                        stocks = backendViewModel.getStocks(newText)?.tickers
                    } catch(e: IllegalArgumentException) {
                        Handler(Looper.getMainLooper()).post {
                            stockSearchAdapter.replace(ArrayList())
                            stockSearchAdapter.notifyDataSetInvalidated()
                            searchList.invalidateViews()
                        }
                    }
                    if (stocks != null) {
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}













//        val startTime = System.nanoTime()
//
//        println("TESTING BEFORE 123")
//
//        createSearchRecycler()
//        setupRecentlyClickedRecyclerView()
//
//        println("TESTING AFTER 123")
//
//        val endTime = System.nanoTime()
//        val elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0 // Convert to seconds
//        println("Elapsed time: $elapsedTimeInSeconds seconds")




//    private fun getRecyclerData() {
//        for (i in list.indices){
//            val dataClass = StockSearchDataClass( list[i], descList[0], listTickers[i])
//            dataList.add(dataClass)
//        }
//        searchList.addAll(dataList)
//        recyclerView.adapter = StockSearchAdapterClass(searchList)
//    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createSearchRecycler(){
//        stockViewModel.stockData.observe(viewLifecycleOwner, Observer { stockResponse ->
//            stockResponse?.let {
//                it.results.forEach { result ->
//                    val stringtmp = "${result.T}        Open: ${result.o}        Close: ${result.c}"
//                    list.add(stringtmp)
//                    listTickers.add(result.T)
//                }
//                if(list.isNotEmpty()){
//                    setupRecyclerView() // Update the RecyclerView
//                }
//            } ?: println("Error: Could not fetch stock data")
//        })
//    }
//
//    private fun setupRecyclerView(){
//        //imageList = arrayOf(R.drawable.invis)
//
//        titleList = arrayOf("Blah 1", "Blah 2", "Blah 3",)
//
//        descList = arrayOf(getString(R.string.testString))
//
//        //detailImageList = arrayOf(
//        //    R.drawable.invis
//        //)
//
//        // HERE I WANT TO GET A VIEW ON Fragment_search.xml as findviewby id is red with an error
//
//        recyclerView = binding.stockRecycleView
//        searchView = binding.search
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.setHasFixedSize(true)
//
//        dataList = arrayListOf<StockSearchDataClass>()
//        searchList = arrayListOf<StockSearchDataClass>()
//
//
//        getRecyclerData()
//
//        searchView.clearFocus()
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchView.clearFocus()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                searchList.clear()
//                val searchText = newText!!.toLowerCase(Locale.getDefault())
//                if (searchText.isNotEmpty()){
//                    dataList.forEach{
//                        if (it.dataTitle.toLowerCase(Locale.getDefault()).contains(searchText)){
//                            searchList.add(it)
//                        }
//                    }
//                    recyclerView.adapter!!.notifyDataSetChanged()
//                } else {
//                    searchList.clear()
//                    searchList.addAll(dataList)
//                    recyclerView.adapter!!.notifyDataSetChanged()
//                }
//                return false
//            }
//        })
//        myAdapter = StockSearchAdapterClass(searchList)
//        recyclerView.adapter = myAdapter
//
//        myAdapter.onItemClick = {clickedItem ->
//            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
//
//            intent.putExtra("android", clickedItem)
//            intent.putExtra("stockInfo", clickedItem.dataTitle)
//            startActivity(intent)
//
//            recentlyClickedList.add(0, clickedItem) // Add to the top of the list
//            if (recentlyClickedList.size > 3) { // Limit the list to 10 items, if desired
//                recentlyClickedList.removeAt(recentlyClickedList.size - 1)
//            }
//            recentlyClickedAdapter.notifyDataSetChanged()
//
//        }
//    }

//    private fun setupRecentlyClickedRecyclerView() {
//        recentlyClickedRecyclerView = binding.stockRecycleViewRecent
//        recentlyClickedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recentlyClickedRecyclerView.setHasFixedSize(true)
//
//        recentlyClickedAdapter = StockSearchAdapterClass(recentlyClickedList)
//        recentlyClickedRecyclerView.adapter = recentlyClickedAdapter
//
//
//        recentlyClickedAdapter.onItemClick = { clickedItem ->
//            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
//            intent.putExtra("android", clickedItem)
//            startActivity(intent)
//        }
//    }





//    private val stockViewModel: StockApiViewModel by viewModels()
//    val list: MutableList<String> = ArrayList()
//    val listTickers: MutableList<String> = ArrayList()

//    private lateinit var recyclerView: RecyclerView
//    private lateinit var dataList: ArrayList<StockSearchDataClass>
//
//    private lateinit var recentlyClickedRecyclerView: RecyclerView
//    private lateinit var recentlyClickedAdapter: StockSearchAdapterClass
//    private val recentlyClickedList = ArrayList<StockSearchDataClass>()
//
//    lateinit var imageList: Array<Int>
//    lateinit var titleList: Array<String>
//    lateinit var descList: Array<String>
//    lateinit var detailImageList: Array<Int>
//    private lateinit var myAdapter: StockSearchAdapterClass
//
//    private lateinit var searchView: SearchView
//    private lateinit var searchList: ArrayList<StockSearchDataClass