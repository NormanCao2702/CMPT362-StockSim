package com.example.cmpt362_stocksim

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_stocksim.databinding.FragmentSearchBinding
import java.util.Locale


class StockDetailedEntry : AppCompatActivity() {

    private lateinit var bckButton: Button
    private lateinit var newsBut: Button
    private lateinit var infoBut: Button
    private lateinit var buyBut: Button
    private lateinit var sellBut: Button
    private lateinit var priceText: TextView

    private val stockViewModel: StockApiViewModel by viewModels()

    private var address1 = ""
    private var city = ""
    private var state = ""
    private var currency = ""
    private var desc = ""
    private var homepageURL = ""
    private var listDate = ""
    private var marketCap = ""
    private var name = ""
    private var phone = ""
    private var tolEmployee = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detailed_entry)

        bckButton = findViewById(R.id.entryBackButton)
        infoBut = findViewById(R.id.button)
        newsBut = findViewById(R.id.button2)
        buyBut = findViewById(R.id.button3)
        sellBut = findViewById(R.id.button4)
        priceText = findViewById(R.id.textView)

        val getData2 = intent.getStringExtra("stockInfo")
        closePart = getData2?.split(", Close:")?.get(1)?.trim().toString()

        price = closePart.toDoubleOrNull()!!

        priceText.text = "Price: ${closePart}"

        val getData = intent.getParcelableExtra<StockSearchDataClass>("android")
        if (getData != null){
            val detailTitle: TextView = findViewById(R.id.detailedTitle)
            val detailDesc: TextView = findViewById(R.id.detailedDescription)
            val detailImage: ImageView = findViewById(R.id.detailImage)

            detailTitle.text = getData.ticker
            detailDesc.text = getData.dataDesc
            detailImage.setImageResource(getData.dataDetailImage)


            getInfoData(getData.ticker)
            getNewsData(getData.ticker)
        }

        if(savedInstanceState!=null){
            isShowingDialog = savedInstanceState.getBoolean("IS_SHOWING_DIALOG", false);
            if(isShowingDialog){
                if (dialogHelper.whichDialog == 0){
                    infoDialog()
                } else if (dialogHelper.whichDialog == 1){
                    newsDialog()
                } else if (dialogHelper.whichDialog == 2){
                    if (getData != null) {
                        buyDialog(getData.ticker)
                    }
                } else if (dialogHelper.whichDialog == 3){
                    if (getData != null) {
                        sellDialog(getData.ticker)
                    }
                }
            }
        }


        bckButton.setOnClickListener{
            this.finish()
        }


        infoBut.setOnClickListener{
            infoDialog()
        }

        newsBut.setOnClickListener{
            newsDialog()
        }

        buyBut.setOnClickListener{
            if (getData != null) {
                buyDialog(getData.ticker)
            }
        }

        sellBut.setOnClickListener{
            if (getData != null) {
                sellDialog(getData.ticker)
            }
        }

    }

    // Used to save the dialogs for when they are rotated.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("IS_SHOWING_DIALOG", isShowingDialog)
        super.onSaveInstanceState(outState)
    }

    private fun getNewsData(ticker: String){
        stockViewModel.getStockData3(ticker).observe(this, Observer { stockResponse ->
            stockResponse?.let {
                it.results.forEach { result ->
                    idList.add(result.id)
                    publisherNList.add(result.publisher.name)
                    publisherURLList.add(result.publisher.homepage_url)
                    titleList.add(result.title)
                    authorList.add(result.author)
                    published_utcList.add(result.published_utc)
                    article_urlList.add(result.article_url)
                    image_urlList.add(result.image_url)
                    descriptionList.add(result.description)
                }
            } ?: println("Error: Could not fetch stock data")
        })
    }



    private fun getInfoData(ticker: String){
        stockViewModel.getStockData2(ticker).observe(this, Observer { stockResponse ->
            stockResponse?.let {
                val result = it.results // 'results' is now an object, not a list

                // Access fields from the result directly
                address1 = result.address.address1
                city = result.address.city
                state = result.address.state
                currency = result.currency_name
                desc = result.description
                homepageURL = result.homepage_url
                listDate = result.list_date
                marketCap = result.market_cap.toString()
                name = result.name
                phone = result.phone_number
                tolEmployee = result.total_employees.toString()

            } ?: println("Error: Could not fetch stock data")
        })
    }

    private fun buyDialog(ticker: String){
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 2

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)

        val builder = AlertDialog.Builder(this).setView(view)
        val editText = EditText(this)

        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Buy Stock")
            setMessage("How much stock would you like to buy?")
            setView(editText)

            setPositiveButton("Ok"){ _, _ ->
                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }

                // GET THE BUY AMOUNT HERE
                var value = text.toInt()


                // price var in global has the price of the ticker
                // ALSO USE ticker variable to get the ticker name for database



                isShowingDialog = false
            }
            setNegativeButton("Cancel"){ _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }

    private fun sellDialog(ticker: String){
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 3

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog_buysell, null)
        val builder = AlertDialog.Builder(this).setView(view)

        val editText = EditText(this)
        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Sell Stock")
            setView(editText)
            setMessage("How much stock would you like to sell?")

            setPositiveButton("Ok"){ _, _ ->

                var text = editText.text.toString()
                if (text.isEmpty()) {
                    text = "0"
                }

                // GET THE BUY AMOUNT HERE
                var value = text.toInt()
                // price var in global has the price of the ticker
                // ALSO USE ticker variable to get the ticker name for database

                isShowingDialog = false
            }
            setNegativeButton("Cancel"){ _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }





    private fun infoDialog(){
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 0

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog, null)

        val builder = AlertDialog.Builder(this).setView(view)

        // With the dialog box, customize and set toasts for positive and negative button presses
        with(builder) {
            setTitle("Company Info")

            val infoTextView: TextView = view.findViewById(R.id.textvDialog)

            infoTextView.text = "Name: ${name} \n\nAddress:\n${address1}, ${city}, ${state}\n\nPhone:\n" +
                    "${phone}\n\nCurrency:\n${currency}\n\nHomepage:\n${homepageURL}\n\nList Date:\n${listDate}\n\nMarketCap:\n${marketCap}\n\nTotal Employee:\n${tolEmployee}" +
                    "\n\nDescription:\n${desc}"

            setPositiveButton("Ok"){ _, _ ->
                isShowingDialog = false
            }
            setNegativeButton("Cancel"){ _, _ ->
                isShowingDialog = false
            }
            show()
        }
    }


    private fun newsDialog(){
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

            newsTextView.text = "Article: ${titleList[0]} \n\nAuthor:\n${authorList[0]} \n\nPublish Date:\n" +
                    "${published_utcList[0]}\n\nDescription:\n${descriptionList[0]}\n\nLink:\n${article_urlList[0]}\n\nPublisher:\n${publisherNList[0]}"

            newsTextView2.text = "Article: ${titleList[1]} \nAuthor:\n${authorList[1]} \nPublish Date:\n" +
                    "${published_utcList[1]}\n\nDescription:\n${descriptionList[1]}\n\nLink:\n${article_urlList[1]}\n\nPublisher:\n${publisherNList[1]}"

            newsTextView3.text = "Article: ${titleList[2]} \nAuthor:\n${authorList[2]} \nPublish Date:\n" +
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

            setPositiveButton("Ok"){ _, _ ->
                isShowingDialog = false
            }
            setNegativeButton("Cancel"){ _, _ ->
                isShowingDialog = false
            }
            show()
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

