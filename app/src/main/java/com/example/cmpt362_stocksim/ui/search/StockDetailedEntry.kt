package com.example.cmpt362_stocksim.ui.search

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

class StockDetailedEntry : AppCompatActivity() {

    private lateinit var bckButton: Button
    private lateinit var newsBut: Button
    private lateinit var infoBut: Button
    private lateinit var buyBut: Button
    private lateinit var sellBut: Button
    private lateinit var recommendBut: Button
    private lateinit var endoText: TextView
    private lateinit var infoTextView: TextView
    private lateinit var titletext2: TextView

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

    private var idList: MutableList<String> = ArrayList()
    private var publisherNList: MutableList<String> = ArrayList()
    private var publisherURLList: MutableList<String> = ArrayList()
    private var titleList: MutableList<String> = ArrayList()
    private var authorList: MutableList<String> = ArrayList()
    private var published_utcList: MutableList<String> = ArrayList()
    private var article_urlList: MutableList<String> = ArrayList()
    private var image_urlList: MutableList<String> = ArrayList()
    private var descriptionList: MutableList<String> = ArrayList()
    private var price: Double = 0.0
    private var closePart = ""
    var isShowingDialog: Boolean = false

    val repository2 = BackendRepository()
    val viewModelFactory2 = BackendViewModelFactory(repository2)
    val backendViewModel = viewModelFactory2.create(BackendViewModel::class.java)

    val api = ""

    private val userDataManager by lazy { UserDataManager(this) }

    private lateinit var lineChart: LineChart
    val entries2 = ArrayList<Entry>()
    var daysMax = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detailed_entry)

        bckButton = findViewById(R.id.entryBackButton)
        //infoBut = findViewById(R.id.button)
        newsBut = findViewById(R.id.button2)
        buyBut = findViewById(R.id.button3)
        sellBut = findViewById(R.id.button4)

        recommendBut = findViewById(R.id.button22)
        endoText = findViewById(R.id.textViewRec)
        titletext2 = findViewById(R.id.detailedTitle2)


        val getData = intent.getStringExtra("tickerName")

        if (getData != null){
            val detailTitle: TextView = findViewById(R.id.detailedTitle)
            val detailDesc: TextView = findViewById(R.id.detailedDescription)
            val iconView: ImageView = findViewById(R.id.detailImage2)
            //infoTextView = findViewById(R.id.textvDialog)
            //val detailImage: ImageView = findViewById(R.id.detailImage)

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

            lineChart = findViewById(R.id.lineChart2) // Make sure you have this ID in your layout
            setupChart(getData)

            loadImageWithPicasso("https://stocksim.breadmod.info/api/ticker/icon?symbol=${getData}", iconView)

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

            getInfoData(getData, detailDesc)
            getNewsData(getData)
        }

        if (savedInstanceState != null) {
            isShowingDialog = savedInstanceState.getBoolean("IS_SHOWING_DIALOG", false);
            if (isShowingDialog) {
                if (dialogHelper.whichDialog == 0) {
                   // infoDialog()
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

        bckButton.setOnClickListener {
            this.finish()
        }

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



    private fun getNewsData(ticker: String) {

        lifecycleScope.launch {
            try {
                val result = backendViewModel.getNews(ticker)
                if (result != null) {

                    val newsList = result.results

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
            } catch (e: IllegalArgumentException) {
                Log.d("MJR", e.message!!)
            }
        }
    }


    private fun getInfoData(ticker: String, descView: TextView) {
        lifecycleScope.launch {
            try {
                val response = backendViewModel.getInfo(ticker)
                if (response != null) {
                    // Access fields from the result directly
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

            descView.text = "Name: ${name} \n\nAddress:\n${address1}, ${city}, ${state}\n\nPhone:\n" +
                    "${phone}\n\nPostal Code:\n${polCode}\n\nHomepage:\n${homepageURL}\n\nList Date:\n${listDate}\n\nMarketCap:\n $${marketCap}\n\nTotal Employees:\n${tolEmployee}" +
                    "\n\nDescription:\n${desc}"

        }

    }

    private fun buyDialog(ticker: String) {
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 2

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)

        val builder = AlertDialog.Builder(this).setView(view)
        val editText = EditText(this)

        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)
        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Buy Stock")
            setMessage("How much stock would you like to buy?")
            setView(editText)

            setPositiveButton("Ok") { _, _ ->
                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }
                // GET THE BUY AMOUNT HERE


                lifecycleScope.launch {
                    try {
                        val token = userDataManager.getJwtToken()
                        val response = token?.let { backendViewModel.buyStock(ticker, text, it) }
                        if (response != null) {
                           println("BOUGHT STONKS")
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

    private fun sellDialog(ticker: String) {
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 3

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)
        val builder = AlertDialog.Builder(this).setView(view)


        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val startStocks = sharedPreferences.getFloat("stockCount", 0.0F)

        val editText = EditText(this)
        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Sell Stock")
            setView(editText)
            setMessage("How much stock would you like to sell?")

            setPositiveButton("Ok") { _, _ ->

                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }



                //var value = text.toInt()

                // Post sell amount and get that difference in cash
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


//    private fun infoDialog() {
//        // Set heart rate dialog to active
//        isShowingDialog = true
//        dialogHelper.whichDialog = 0
//
//        // Create view and dialog builder
//        val view = layoutInflater.inflate(R.layout.edittext_dialog, null)
//
//        val builder = AlertDialog.Builder(this).setView(view)
//
//        // With the dialog box, customize and set toasts for positive and negative button presses
//        with(builder) {
//            setTitle("Company Info")
//
//            val infoTextView: TextView = view.findViewById(R.id.textvDialog)
//
//            infoTextView.text =
//                "Name: ${name} \n\nAddress:\n${address1}, ${city}, ${state}\n\nPhone:\n" +
//                        "${phone}\n\nCurrency:\n${currency}\n\nHomepage:\n${homepageURL}\n\nList Date:\n${listDate}\n\nMarketCap:\n${marketCap}\n\nTotal Employee:\n${tolEmployee}" +
//                        "\n\nDescription:\n${desc}"
//
//            setPositiveButton("Ok") { _, _ ->
//                isShowingDialog = false
//            }
//            setNegativeButton("Cancel") { _, _ ->
//                isShowingDialog = false
//            }
//            show()
//        }
//    }


    private fun newsDialog() {
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 1

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_news, null)
        //val editT = view.findViewById<EditText>(R.id.editTextDialog) This is used to get the result when database is implemented
        val builder = AlertDialog.Builder(this).setView(view)

        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Company News")

            val newsTextView: TextView = view.findViewById(R.id.textvDialog)
            val newsTextView2: TextView = view.findViewById(R.id.textvDialog2)
            val newsTextView3: TextView = view.findViewById(R.id.textvDialog3)

            newsTextView.text =
                "Article: ${titleList[0]} \n\nAuthor:\n${authorList[0]} \n\nPublish Date:\n" +
                        "${published_utcList[0]}\n\nDescription:\n${descriptionList[0]}\n\nLink:\n${article_urlList[0]}\n\nPublisher:\n${publisherNList[0]}"

            newsTextView2.text =
                "Article: ${titleList[1]} \nAuthor:\n${authorList[1]} \nPublish Date:\n" +
                        "${published_utcList[1]}\n\nDescription:\n${descriptionList[1]}\n\nLink:\n${article_urlList[1]}\n\nPublisher:\n${publisherNList[1]}"

            newsTextView3.text =
                "Article: ${titleList[2]} \nAuthor:\n${authorList[2]} \nPublish Date:\n" +
                        "${published_utcList[2]}\n\nDescription:\n${descriptionList[2]}\n\nLink:\n${article_urlList[2]}\n\nPublisher:\n${publisherNList[2]}"

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

    fun loadImageWithPicasso(imageUrl: String, imageView: ImageView,) {
        val client = OkHttpClient.Builder()
            .build()

        // Picasso builder with authentication headers
        val picasso = Picasso.Builder(imageView.context)
            .downloader(OkHttp3Downloader(client))
            .build()

        picasso.load(imageUrl).into(imageView)
    }


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

    private fun setData(ticker: String) {

        // Create flat line data points for 7 days

        var entries = ArrayList<BackendRepository.historyObject>()
        var days = 1000
        daysMax = 1000

        lifecycleScope.launch {
            try {
                val response = backendViewModel.getHistory(ticker)
                if (response != null) {
                    entries = response.history
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



    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    object dialogHelper {
        var whichDialog: Int = 0
    }
}
