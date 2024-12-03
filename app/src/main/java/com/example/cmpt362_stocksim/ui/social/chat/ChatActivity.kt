package com.example.cmpt362_stocksim.ui.social.chat

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.cmpt362_stocksim.api.BackendRepository
import com.example.cmpt362_stocksim.api.BackendViewModel
import com.example.cmpt362_stocksim.api.BackendViewModelFactory
import com.example.cmpt362_stocksim.R
import com.example.cmpt362_stocksim.ui.social.profile.ProfileActivity
import com.example.cmpt362_stocksim.userDataManager.UserDataManager
import kotlinx.coroutines.launch

/**
 * ChatActivity handles the user-to-user chat interface.
 * This activity displays a chat window and allows users to send and receive messages in real-time.
 */
class ChatActivity : AppCompatActivity() {
    // ID and name of the user being chatted with
    private var other_id = 0
    private lateinit var other_name: String
    // UI components
    private lateinit var usernameTw: TextView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var sendMessageFL: FrameLayout
    private lateinit var backButtonIB: AppCompatImageButton
    private lateinit var messageText: EditText
    // List to store chat messages
    private val messages = ArrayList<BackendRepository.message>()

    // Backend ViewModel for handling API interactions
    private lateinit var backendViewModel: BackendViewModel
    private lateinit var chatView: RecyclerView
    private var latestMessage = 1

    // Lazy initialization of UserDataManager for user-related data
    private val userDataManager by lazy { UserDataManager(this) }
    private lateinit var repeatHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        // Retrieve user ID and name from the intent
        other_id = intent.extras?.getInt("USER_ID")!!
        other_name = intent.extras?.getString("USERNAME")!!

        // Initialize UI components
        usernameTw = findViewById(R.id.senderName)
        chatView = findViewById(R.id.chatList)
        sendMessageFL = findViewById(R.id.layoutSend)
        backButtonIB = findViewById(R.id.backButton)
        messageText = findViewById(R.id.inputMessage)

        // Set the username text and make it clickable to navigate to the user's profile
        usernameTw.setText(other_name)
        usernameTw.setOnClickListener {
            val args = Bundle()
            val intent = Intent(this, ProfileActivity::class.java)
            args.putInt("USER_ID", other_id)
            intent.putExtras(args)
            startActivity(intent)
        }
        setTitle("Chat")

        // Initialize the ViewModel for backend interactions
        backendViewModel =
            BackendViewModelFactory(BackendRepository()).create(BackendViewModel::class.java)

        chatAdapter = ChatAdapter(messages, userDataManager.getUserId()?.toInt()!!)
        chatView.adapter = chatAdapter

        // Send button functionality
        sendMessageFL.setOnClickListener {
            val text = messageText.text.toString()
            if (!text.isBlank()) {
                lifecycleScope.launch {
                    val token = userDataManager.getJwtToken()!!
                    try {
                        backendViewModel.setSendMessage(other_id.toString(), text, token)
                        Handler(Looper.getMainLooper()).post {
                            messageText.setText("")
                            messageText.clearFocus()
                            loadNewMessages()
                        }
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this@ChatActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        backButtonIB.setOnClickListener {
            finish()
        }
        loadNewMessages()
    }

    // Schedules a repeating action to execute with a delay.
    fun repeatDelayed(delay: Long, action: () -> Unit): Handler {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                action()
                handler.postDelayed(this, delay)
            }
        }, delay)
        return handler
    }

    override fun onStart() {
        super.onStart()
        repeatHandler = repeatDelayed(5000) { loadNewMessages() }
    }

    override fun onStop() {
        super.onStop()
        repeatHandler.removeCallbacksAndMessages(null)
    }

    //Fetches new messages from the backend and updates the chat UI.
    fun loadNewMessages() {
        lifecycleScope.launch {
            val token = userDataManager.getJwtToken()!!
            val newMessages = backendViewModel.getCheckMessages(
                other_id.toString(),
                latestMessage.toString(),
                token
            )?.messages!!
            if (newMessages.size > 0) {
                latestMessage = newMessages.get(newMessages.size - 1).message_id
                runOnUiThread {
                    messages.addAll(newMessages)
                    chatAdapter.notifyItemRangeInserted(
                        messages.size - newMessages.size,
                        newMessages.size
                    )
                }
            }
        }

    }
}