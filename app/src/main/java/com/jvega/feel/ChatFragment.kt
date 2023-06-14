package com.jvega.feel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jvega.feel.util.TokenManager
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class ChatWebSocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        // Connection is established
        println("chat onOpen")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // Handle received message
        println("chat onMessage")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        // Connection is closed
        println("chat onClosed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // Handle connection failure
        println("chat onFailure: ${t.message}")
    }
}

private const val NORMAL_CLOSURE_STATUS = 1000


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    private var webSocket: WebSocket? = null

    private val roomName = "asssd" // Replace with the actual room name
    private val url = "192.168.11.4:8000"
    private val wsUrl = "ws://$url/ws/chat/$roomName/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("onCreateView chat",TokenManager.getToken().toString())
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated chat")
        val client = OkHttpClient()
        println("onViewCreated2 chat")
        // Create request object for WebSocket connection
        val request = Request.Builder()
            .url(wsUrl)
            .build()
        println("onViewCreated3 chat")
        // Create WebSocket instance and establish connection
        try {
            println(wsUrl)
            webSocket = client.newWebSocket(request, ChatWebSocketListener())
            println(webSocket)
        } catch (e: Exception) {
            // Log the error
            println("onViewCreated3 error chat")
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        println("onDestroyView chat")
        super.onDestroyView()
        // Close the WebSocket connection when the fragment is destroyed
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
    }
}