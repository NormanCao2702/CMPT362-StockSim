package com.example.cmpt362_stocksim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.text.SpannableString
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_stocksim.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch
import java.util.Locale


class SearchFragment: Fragment()  {
    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val stockViewModel: StockApiViewModel by viewModels()
    val list: MutableList<String> = ArrayList()
    val listTickers: MutableList<String> = ArrayList()

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<StockSearchDataClass>

    private lateinit var recentlyClickedRecyclerView: RecyclerView
    private lateinit var recentlyClickedAdapter: StockSearchAdapterClass
    private val recentlyClickedList = ArrayList<StockSearchDataClass>()

    lateinit var imageList: Array<Int>
    lateinit var titleList: Array<String>
    lateinit var descList: Array<String>
    lateinit var detailImageList: Array<Int>
    private lateinit var myAdapter: StockSearchAdapterClass

    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<StockSearchDataClass>

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

        createSearchRecycler()
        setupRecentlyClickedRecyclerView()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRecyclerData() {
        for (i in list.indices){
            val dataClass = StockSearchDataClass( list[i], descList[0], listTickers[i])
            dataList.add(dataClass)
        }

        searchList.addAll(dataList)
        recyclerView.adapter = StockSearchAdapterClass(searchList)
    }


    private fun createSearchRecycler(){
        stockViewModel.stockData.observe(viewLifecycleOwner, Observer { stockResponse ->
            stockResponse?.let {
                it.results.forEach { result ->
                    val stringtmp = "${result.T}        Open: ${result.o}        Close: ${result.c}"
                    list.add(stringtmp)
                    listTickers.add(result.T)
                }
                if(list.isNotEmpty()){
                    setupRecyclerView() // Update the RecyclerView
                }
            } ?: println("Error: Could not fetch stock data")
        })
    }

    private fun setupRecyclerView(){
        //imageList = arrayOf(R.drawable.invis)

        titleList = arrayOf("Blah 1", "Blah 2", "Blah 3",)

        descList = arrayOf(getString(R.string.testString))

        //detailImageList = arrayOf(
        //    R.drawable.invis
        //)

        // HERE I WANT TO GET A VIEW ON Fragment_search.xml as findviewby id is red with an error

        recyclerView = binding.stockRecycleView
        searchView = binding.search

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<StockSearchDataClass>()
        searchList = arrayListOf<StockSearchDataClass>()


        getRecyclerData()

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    dataList.forEach{
                        if (it.dataTitle.toLowerCase(Locale.getDefault()).contains(searchText)){
                            searchList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    searchList.clear()
                    searchList.addAll(dataList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        myAdapter = StockSearchAdapterClass(searchList)
        recyclerView.adapter = myAdapter

        myAdapter.onItemClick = {clickedItem ->
            val intent = Intent(requireContext(), StockDetailedEntry::class.java)

            intent.putExtra("android", clickedItem)
            intent.putExtra("stockInfo", clickedItem.dataTitle)
            startActivity(intent)

            recentlyClickedList.add(0, clickedItem) // Add to the top of the list
            if (recentlyClickedList.size > 3) { // Limit the list to 10 items, if desired
                recentlyClickedList.removeAt(recentlyClickedList.size - 1)
            }
            recentlyClickedAdapter.notifyDataSetChanged()

        }
    }

    private fun setupRecentlyClickedRecyclerView() {
        recentlyClickedRecyclerView = binding.stockRecycleViewRecent
        recentlyClickedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentlyClickedRecyclerView.setHasFixedSize(true)

        recentlyClickedAdapter = StockSearchAdapterClass(recentlyClickedList)
        recentlyClickedRecyclerView.adapter = recentlyClickedAdapter


        recentlyClickedAdapter.onItemClick = { clickedItem ->
            val intent = Intent(requireContext(), StockDetailedEntry::class.java)
            intent.putExtra("android", clickedItem)
            startActivity(intent)
        }
    }

}