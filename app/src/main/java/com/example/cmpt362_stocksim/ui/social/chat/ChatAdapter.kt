package com.example.cmpt362_stocksim.ui.social.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_stocksim.BackendRepository
import com.example.cmpt362_stocksim.R

class ChatAdapter(private val dataSet: ArrayList<BackendRepository.message>, private val our_id: Int) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(view: View, we_sent: Boolean) : RecyclerView.ViewHolder(view) {
        val text: TextView
        val messageDate: TextView

        init {
            if(we_sent) {
                text = view.findViewById(R.id.textMessageSent)
            } else {
                text = view.findViewById(R.id.textMessageReceived)
            }
            messageDate = view.findViewById(R.id.textDateTime)
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val inflater = LayoutInflater.from(viewGroup.context)
        var viewHolder: ViewHolder? = null
        // Sent from us
        if(viewType == 0) {
            val view = inflater.inflate(R.layout.item_container_sent_message, viewGroup,  false)
            viewHolder = ViewHolder(view, true)
        }
        // Sent from other user
        else {
            val view = inflater.inflate(R.layout.item_container_received_message, viewGroup,  false)
            viewHolder = ViewHolder(view, false)
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.text.setText(dataSet.get(position).content)
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        val from = dataSet.get(position).from
        if(from == our_id) {
            return 0
        }
        return from
    }


}