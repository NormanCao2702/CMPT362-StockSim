package com.example.cmpt362_stocksim.ui.social.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.R

/**
 * ChatAdapter is a custom RecyclerView.Adapter implementation that handles displaying chat messages
 * in a conversation between two users. It supports differentiating between sent and received messages.
 */
class ChatAdapter(private val dataSet: ArrayList<BackendRepository.message>, private val our_id: Int) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    /**
     * ViewHolder represents the visual components for each chat message.
     * Depending on whether the message was sent or received, it binds to a different layout.
     */
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
    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * Inflates the appropriate layout based on the message type (sent or received).
     */
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

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Binds the message content to the ViewHolder.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.text.setText(dataSet.get(position).content)
    }

    override fun getItemCount() = dataSet.size

    /**
     * Returns the view type of the item at the given position.
     * 0 indicates a message sent by the current user, while any other value indicates a received message.
     */
    override fun getItemViewType(position: Int): Int {
        val from = dataSet.get(position).from
        if(from == our_id) {
            return 0
        }
        return from
    }


}