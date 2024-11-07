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

    var isShowingDialog: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_detailed_entry)

        bckButton = findViewById(R.id.entryBackButton)
        newsBut = findViewById(R.id.button)


        val getData = intent.getParcelableExtra<StockSearchDataClass>("android")
        if (getData != null){
            val detailTitle: TextView = findViewById(R.id.detailedTitle)
            val detailDesc: TextView = findViewById(R.id.detailedDescription)
            val detailImage: ImageView = findViewById(R.id.detailImage)

            detailTitle.text = getData.ticker
            detailDesc.text = getData.dataDesc
            detailImage.setImageResource(getData.dataDetailImage)


            getInfoData(getData.ticker)
        }

        if(savedInstanceState!=null){
            isShowingDialog = savedInstanceState.getBoolean("IS_SHOWING_DIALOG", false);
            if(isShowingDialog){
                if (dialogHelper.whichDialog == 0){
                    infoDialog()
                }
            }
        }


        bckButton.setOnClickListener{
            this.finish()
        }



        newsBut.setOnClickListener{
            infoDialog()
        }


    }

    // Used to save the dialogs for when they are rotated.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("IS_SHOWING_DIALOG", isShowingDialog)
        super.onSaveInstanceState(outState)
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


    private fun infoDialog(){
        // Set heart rate dialog to active
        isShowingDialog = true
        dialogHelper.whichDialog = 0

        // Create view and dialog builder
        val view = layoutInflater.inflate(R.layout.edittext_dialog, null)
        //val editT = view.findViewById<EditText>(R.id.editTextDialog) This is used to get the result when database is implemented
        val builder = AlertDialog.Builder(this).setView(view)


        //val editText = EditText(this)

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




    object dialogHelper {
        var whichDialog: Int = 0
    }
}

