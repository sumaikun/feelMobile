package com.jvega.feel.chat


sealed class ChatItem {
    data class MessageItem(val id: String, val message: String, val isMine: Boolean) : ChatItem()
    data class DateItem(val date: String) : ChatItem()
}