package com.jvega.feel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jvega.feel.chat.ChatAdapter
import com.jvega.feel.chat.ChatItem
import com.jvega.feel.util.TokenManager
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.*

private const val NORMAL_CLOSURE_STATUS = 1000


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private var webSocket: WebSocket? = null

    private val roomName = "testingRoom" // Replace with the actual room name
    private val url = "192.168.11.2:8000"
    private val wsUrl = "ws://$url/ws/chat/$roomName/"
    private val chatList = mutableListOf<ChatItem>()
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("onCreateView chat", TokenManager.getToken().toString())
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    fun generateUniqueMessageId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //println("onViewCreated chat")
        val client = OkHttpClient()
        //println("onViewCreated2 chat")
        // Create request object for WebSocket connection
        val request = Request.Builder()
            .url(wsUrl)
            .build()
        //println("onViewCreated3 chat")
        // Create WebSocket instance and establish connection
        try {
            println(wsUrl)
            val listener = object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    // Message received. Parse it as JSON and update UI accordingly
                    val gson = Gson()
                    val message = gson.fromJson(text, ChatItem.MessageItem::class.java)
                    println(message)
                    activity?.runOnUiThread {
                        // Update UI in UI thread
                        val existingMessage =
                            chatList.find { it is ChatItem.MessageItem && it.id == message.id  }
                        if (existingMessage == null) {
                            activity?.runOnUiThread {
                                chatList.add(message)
                                chatAdapter.notifyDataSetChanged()
                            }
                        }
                        chatList.add(message)
                        chatAdapter.notifyDataSetChanged()
                    }
                }
            }
            webSocket = client.newWebSocket(request, listener)
            println(webSocket)

        } catch (e: Exception) {
            // Log the error
            println("onViewCreated3 error chat")
            e.printStackTrace()
        }

        messageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        chatAdapter = ChatAdapter(chatList)
        messageRecyclerView.adapter = chatAdapter
        chatList.add(ChatItem.DateItem("27 June"))
        //chatList.add(ChatItem.MessageItem("Hello, world!", true))
        //chatList.add(ChatItem.MessageItem("How are you?", false))
        //chatList.add(ChatItem.MessageItem("I'm fine, thank you.", true))
        chatAdapter.notifyDataSetChanged()

        val sendButton: Button = view.findViewById(R.id.sendButton)
        val inputEditText: EditText = view.findViewById(R.id.inputEditText)
        sendButton.setOnClickListener {
            val message = inputEditText.text.toString()
            //Log.d("chat", message)
            inputEditText.text.clear()
            val messageId = generateUniqueMessageId()
            val id = generateUniqueMessageId()
            chatList.add(ChatItem.MessageItem(id, message, true))
            chatAdapter.notifyItemInserted(chatList.size - 1)
            messageRecyclerView.scrollToPosition(chatList.size - 1)
            val gson = Gson()
            val messageJson = gson.toJson(ChatItem.MessageItem(id, message, isMine = true))
            webSocket?.send(messageJson)
        }
    }

    override fun onDestroyView() {
        println("onDestroyView chat")
        super.onDestroyView()
        // Close the WebSocket connection when the fragment is destroyed
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
    }
}