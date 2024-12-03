package com.example.cmpt362_stocksim.ui.search

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import com.github.mikephil.charting.charts.LineChart

/**
 * Activity to display detailed stock information, including price, history, news, and more.
 */
class StockDetailedEntry : AppCompatActivity() {
    // UI elements
    private lateinit var bckButton: ImageButton
    private lateinit var newsBut: Button
    private lateinit var buyBut: Button
    private lateinit var sellBut: Button
    private lateinit var recommendBut: Button
    private lateinit var endoText: TextView
    private lateinit var titletext2: TextView

    // Stock-related fields
    private var address1 = ""
    private var city = ""
    private var state = ""
    private var desc = ""
    private var homepageURL = ""
    private var listDate = ""
    private var marketCap = ""
    private var name = ""
    private var phone = ""
    private var tolEmployee = ""
    private var polCode = ""

    // News data lists
    private var idList: MutableList<String> = ArrayList()
    private var publisherNList: MutableList<String> = ArrayList()
    private var publisherURLList: MutableList<String> = ArrayList()
    private var titleList: MutableList<String> = ArrayList()
    private var authorList: MutableList<String> = ArrayList()
    private var published_utcList: MutableList<String> = ArrayList()
    private var article_urlList: MutableList<String> = ArrayList()
    private var image_urlList: MutableList<String> = ArrayList()
    private var descriptionList: MutableList<String> = ArrayList()

    // Checks to see if a dialog box is showing
    var isShowingDialog: Boolean = false

    // Connect backend API to front end
    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    // User data manager for basic user information and id's ,etc
    private val userDataManager by lazy { UserDataManager(this) }

    // Line chart for displaying stock price history
    private lateinit var lineChart: LineChart

    val entries2 = ArrayList<Entry>()
    var daysMax = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detailed_entry)

        // Initialize UI elements
        bckButton = findViewById(R.id.entryBackButton)
        newsBut = findViewById(R.id.button2)
        buyBut = findViewById(R.id.button3)
        sellBut = findViewById(R.id.button4)
        recommendBut = findViewById(R.id.button22)
        endoText = findViewById(R.id.textViewRec)
        titletext2 = findViewById(R.id.detailedTitle2)

        // Get stock ticker data from the intent
        val getData = intent.getStringExtra("tickerName")

        if (getData != null){
            // Get views
            val detailTitle: TextView = findViewById(R.id.detailedTitle)
            val detailDesc: TextView = findViewById(R.id.detailedDescription)
            val iconView: ImageView = findViewById(R.id.detailImage2)

            // Fetch and display stock price and daily change
            lifecycleScope.launch {
                try {
                    val response = backendViewModel.getPrice(getData)
                    if (response != null) {
                        detailTitle.text = "${getData} - ${response.price}"
                        titletext2.text = "Daily Change: ${response.change}"
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }

            // Set up the stock price history chart
            lineChart = findViewById(R.id.lineChart2) // Make sure you have this ID in your layout
            setupChart(getData)

            // Load company logo using Picasso
            loadImageWithPicasso("https://stocksim.breadmod.info/api/ticker/icon?symbol=${getData}", iconView)

            // Fetch and display endorsement count
            lifecycleScope.launch {
                try {
                    val response = backendViewModel.getEndorse(getData)
                    if (response != null) {
                        val endoCount = response.endorsements
                        endoText.text = "${endoCount} People endorsed this stock"
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
            // Fetch company info and news
            getInfoData(getData, detailDesc)
            getNewsData(getData)
        }

        // Restore dialog state on configuration changes
        if (savedInstanceState != null) {
            isShowingDialog = savedInstanceState.getBoolean("IS_SHOWING_DIALOG", false);
            if (isShowingDialog) {
                if (dialogHelper.whichDialog == 0) {
                } else if (dialogHelper.whichDialog == 1) {
                    newsDialog()
                } else if (dialogHelper.whichDialog == 2) {
                    if (getData != null) {
                        buyDialog(getData)
                    }
                } else if (dialogHelper.whichDialog == 3) {
                    if (getData != null) {
                        sellDialog(getData)
                    }
                }
            }
        }

        // Set up back button
        bckButton.setOnClickListener {
            this.finish()
        }

        // When recommend button is pressed then add your recommend to database and display it
        recommendBut.setOnClickListener() {
            lifecycleScope.launch {
                try {
                    val token = userDataManager.getJwtToken()
                    val sendEndorse = getData?.let { it1 ->
                        if (token != null) {
                            backendViewModel.setEndorse(it1, token)
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
                try {
                    val response = getData?.let { it1 -> backendViewModel.getEndorse(it1) }
                    if (response != null) {

                        val endoCount = response.endorsements
                        endoText.text = "${endoCount} People endorsed this stock"
                    }
                } catch (e: IllegalArgumentException) {
                    Log.d("MJR", e.message!!)
                }
            }
            println("Button clicked and out")
        }

        // Set up buttons
        newsBut.setOnClickListener {
            newsDialog()
        }
        buyBut.setOnClickListener {
            if (getData != null) {
                buyDialog(getData)
            }
        }
        sellBut.setOnClickListener {
            if (getData != null) {
                sellDialog(getData)
            }
        }
    }

    // Used to save the dialogs for when they are rotated.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("IS_SHOWING_DIALOG", isShowingDialog)
        super.onSaveInstanceState(outState)
    }

    // Function to fetch and process news data for a given stock ticker
    private fun getNewsData(ticker: String) {
        lifecycleScope.launch {
            try {
                // Fetch news data from backendViewModel
                val result = backendViewModel.getNews(ticker)
                if (result != null) {
                    val newsList = result.results
                    if(newsList.isEmpty()){
                        // Add placeholder empty values if no news is available
                        repeat(3) {
                            idList.add("")
                            publisherNList.add("")
                            publisherURLList.add("")
                            titleList.add("")
                            authorList.add("")
                            published_utcList.add("")
                            article_urlList.add("")
                            image_urlList.add("")
                            descriptionList.add("")
                        }
                    } else {
                        // Populate lists with news data
                        for (news in newsList) {
                            idList.add(news.id)
                            publisherNList.add(news.publisher.name)
                            publisherURLList.add(news.publisher.homepage_url)
                            titleList.add(news.title)
                            authorList.add(news.author)
                            published_utcList.add(news.published_utc)
                            article_urlList.add(news.article_url)
                            image_urlList.add(news.image_url)
                            descriptionList.add(news.description)
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                println("it returned null")
                Log.d("MJR", e.message!!)
            }
        }
    }

    // Function to fetch and display company information for a stock ticker
    private fun getInfoData(ticker: String, descView: TextView) {
        lifecycleScope.launch {
            try {
                // Fetch company info from backendViewModel
                val response = backendViewModel.getInfo(ticker)
                if (response != null) {
                    // Populate data fields with company info
                    address1 = response.address
                    city = response.city
                    state = response.state
                    polCode = response.postal_code
                    desc = response.description
                    homepageURL = response.homepage
                    listDate = response.list_date
                    marketCap = response.market_cap
                    name = response.name
                    phone = response.phone_number
                    tolEmployee = response.employee_count
                }
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }

            // Update the TextView with company details
            descView.text = "Name: ${name} \n\nAddress:\n${address1}, ${city}, ${state}\n\nPhone:\n" +
                    "${phone}\n\nPostal Code:\n${polCode}\n\nHomepage:\n${homepageURL}\n\nList Date:\n${listDate}\n\nMarketCap:\n $${marketCap}\n\nTotal Employees:\n${tolEmployee}" +
                    "\n\nDescription:\n${desc}"

        }
    }

    // Function to show a dialog for buying stock
    private fun buyDialog(ticker: String) {
        isShowingDialog = true
        dialogHelper.whichDialog = 2

        // Create and configure the dialog
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)
        val builder = AlertDialog.Builder(this).setView(view)
        val editText = EditText(this)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)

        with(builder) {
            setTitle("Buy Stock")
            setMessage("How much stock would you like to buy?")
            setView(editText)

            setPositiveButton("Ok") { _, _ ->
                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }

                Toast.makeText(
                    context,
                    "Bought ${text} stocks",
                   Toast.LENGTH_SHORT
                ).show()

                // Set bought amount to backend
                lifecycleScope.launch {
                    try {
                        val token = userDataManager.getJwtToken()
                        val response = token?.let { backendViewModel.buyStock(ticker, text, it) }
                        if (response != null) {

                        }
                        sharedPreferences.edit().putFloat("stockCount2", (text.toFloat())).apply()
                    } catch (e: IllegalArgumentException) {
                        Log.d("MJR", e.message!!)
                    }
                }
                isShowingDialog = false
            }
            setNegativeButton("Cancel") { _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }

    // Function to show a dialog for selling stock
    private fun sellDialog(ticker: String) {
        isShowingDialog = true
        dialogHelper.whichDialog = 3

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)
        val builder = AlertDialog.Builder(this).setView(view)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)

        val editText = EditText(this)
        with(builder) {
            setTitle("Sell Stock")
            setView(editText)
            setMessage("How much stock would you like to sell?")

            setPositiveButton("Ok") { _, _ ->

                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }

                Toast.makeText(
                    context,
                    "Sold ${text} stocks",
                    Toast.LENGTH_SHORT
                ).show()

                // Set sold amount to backend database
                lifecycleScope.launch {
                    try {
                        val token = userDataManager.getJwtToken()
                        val response = token?.let { backendViewModel.sellStock(ticker, text, it) }
                        if (response != null) {
                        }
                        sharedPreferences.edit().putFloat("stockCount3", (text.toFloat())).apply()

                    } catch (e: IllegalArgumentException) {
                        Log.d("MJR", e.message!!)
                    }
                }
                isShowingDialog = false
            }
            setNegativeButton("Cancel") { _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }

    // Function to display a dialog showing news articles
    private fun newsDialog() {
        isShowingDialog = true
        dialogHelper.whichDialog = 1

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_news, null)
        val builder = AlertDialog.Builder(this).setView(view)

        with(builder) {
            setTitle("Company News")

            // get textviews
            val newsTextView: TextView = view.findViewById(R.id.textvDialog)
            val newsTextView2: TextView = view.findViewById(R.id.textvDialog2)
            val newsTextView3: TextView = view.findViewById(R.id.textvDialog3)

            // set their text with necessary information
            newsTextView.text =
                "Article: ${titleList[0]} \n\nAuthor:\n${authorList[0]} \n\nPublish Date:\n" +
                        "${published_utcList[0]}\n\nDescription:\n${descriptionList[0]}\n\nLink:\n${article_urlList[0]}\n\nPublisher:\n${publisherNList[0]}"

            newsTextView2.text =
                "Article: ${titleList[1]} \nAuthor:\n${authorList[1]} \nPublish Date:\n" +
                        "${published_utcList[1]}\n\nDescription:\n${descriptionList[1]}\n\nLink:\n${article_urlList[1]}\n\nPublisher:\n${publisherNList[1]}"

            newsTextView3.text =
                "Article: ${titleList[2]} \nAuthor:\n${authorList[2]} \nPublish Date:\n" +
                        "${published_utcList[2]}\n\nDescription:\n${descriptionList[2]}\n\nLink:\n${article_urlList[2]}\n\nPublisher:\n${publisherNList[2]}"

            // Set buttons so it takes you to the website
            newsTextView.setOnClickListener {
                openUrl(article_urlList[0])
            }
            newsTextView2.setOnClickListener {
                openUrl(article_urlList[1])
            }
            newsTextView3.setOnClickListener {
                openUrl(article_urlList[2])
            }

            setPositiveButton("Ok") { _, _ ->
                isShowingDialog = false
            }
            setNegativeButton("Cancel") { _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }

    // Function to load an image into an ImageView using Picasso
    fun loadImageWithPicasso(imageUrl: String, imageView: ImageView,) {
        val client = OkHttpClient.Builder()
            .build()

        val picasso = Picasso.Builder(imageView.context)
            .downloader(OkHttp3Downloader(client))
            .build()

        // Load the image into the provided ImageView
        picasso.load(imageUrl).into(imageView)
    }

    // Function to configure and display a line chart for stock data
    private fun setupChart(ticker: String) {
        setData(ticker)
        lineChart.apply {
            // Disable description
            description.isEnabled = false

            // Enable touch gestures
            setTouchEnabled(true)
            setPinchZoom(true)

            // Customize grid background
            setDrawGridBackground(false)

            // Customize X axis
            xAxis.apply {
                setDrawAxisLine(true)
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // Reverse the order to show day 21, day 20, ..., day 1
                        val dayIndex = daysMax - value.toInt()
                        return if (dayIndex in 1..21) {
                            "Day $dayIndex"
                        } else {
                            "" // Prevent invalid labels
                        }
                    }
                }
            }

            // Customize left Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$${value.toInt()}"
                    }
                }
            }

            if (entries2.isNotEmpty()) {
                val prices = entries2.map { it.y }  // Get all the Y values (prices)
                val minPrice = prices.minOrNull() ?: 0f
                val maxPrice = prices.maxOrNull() ?: 0f
                axisLeft.axisMinimum = minPrice * 0.95f  // Add some padding
                axisLeft.axisMaximum = maxPrice * 1.05f  // Add some padding
            }
            // Disable right Y axis
            axisRight.isEnabled = false

            // Customize legend
            legend.isEnabled = false
        }
    }

    // Function to populate data points for the chart
    private fun setData(ticker: String) {

        // Create flat line data points for 7 days
        var entries = ArrayList<BackendRepository.historyObject>()
        var days = 1000
        daysMax = 1000

        // Get datapoints for the selected stock
        lifecycleScope.launch {
            try {
                val response = backendViewModel.getHistory(ticker)
                if (response != null) {
                    entries = response.history
                    entries = entries.reversed() as ArrayList<BackendRepository.historyObject>
                    days = entries.size
                    daysMax = entries.size
                }
            } catch(e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }

            for (entry in entries) {
                entries2.add(Entry(daysMax - days.toFloat(), entry.price))
                days -= 1
            }

            // Create dataset
            val dataSet = LineDataSet(entries2, "Stock Value").apply {
                color = Color.GREEN
                lineWidth = 2f
                setDrawCircles(false)
                setDrawValues(false)
                mode = LineDataSet.Mode.LINEAR

                // Fill color below the line
                setDrawFilled(true)
                fillColor = Color.GREEN
                fillAlpha = 50
            }

            // Set data to chart
            lineChart.data = LineData(dataSet)

            // Animate the chart
            lineChart.animateX(1000)

            // Refresh chart
            lineChart.invalidate()
        }
    }

    // Function to open a URL in the default browser
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    // Helper object to track which dialog is currently active
    object dialogHelper {
        var whichDialog: Int = 0
    }
}

