package com.example.cmpt362_stocksim

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.cmpt362_stocksim.databinding.FragmentStockInventoryBinding

class MyStockInventoryItemRecyclerViewAdapter() : RecyclerView.Adapter<MyStockInventoryItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentStockInventoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 0 //change

    inner class ViewHolder(binding: FragmentStockInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        override fun toString(): String {
            return "" //change
        }
    }

}