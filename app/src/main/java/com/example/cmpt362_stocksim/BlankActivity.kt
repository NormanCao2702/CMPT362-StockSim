package com.example.cmpt362_stocksim

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class BlankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)

        // Handle custom back button click
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}