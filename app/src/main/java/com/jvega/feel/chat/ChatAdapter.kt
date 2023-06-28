package com.jvega.feel.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jvega.feel.R

class ChatAdapter(private val chatList: MutableList<ChatItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_MESSAGE = 0
        const val VIEW_TYPE_DATE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatList[position]) {
            is ChatItem.MessageItem -> VIEW_TYPE_MESSAGE
            is ChatItem.DateItem -> VIEW_TYPE_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE -> MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
            )
            VIEW_TYPE_DATE -> DateViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = chatList[position]) {
            is ChatItem.MessageItem -> (holder as MessageViewHolder).bind(currentItem)
            is ChatItem.DateItem -> (holder as DateViewHolder).bind(currentItem.date)
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatItemLayout: LinearLayout = itemView.findViewById(R.id.chat_item_layout)
        val chatItemLayoutMine: LinearLayout = itemView.findViewById(R.id.chat_item_layout_mine)
        val chatItemText: TextView = itemView.findViewById(R.id.chat_item_text)
        val chatItemTextMine: TextView = itemView.findViewById(R.id.chat_item_text2)

        fun bind(chatMessage: ChatItem.MessageItem) {
            if (chatMessage.isMine) {
                chatItemLayout.visibility = View.GONE
                chatItemLayoutMine.visibility = View.VISIBLE
                chatItemTextMine.text = chatMessage.message
            } else {
                chatItemLayout.visibility = View.VISIBLE
                chatItemLayoutMine.visibility = View.GONE
                chatItemText.text = chatMessage.message
            }
        }
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date_text)

        fun bind(date: String) {
            dateText.text = date
        }
    }

    override fun getItemCount() = chatList.size
}
