package com.simats.ovadrugx

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SupportChatActivity : AppCompatActivity() {
    private lateinit var chatContainer: android.widget.LinearLayout
    private lateinit var chatScrollView: android.widget.ScrollView
    private lateinit var etMessage: android.widget.EditText
    private lateinit var btnSend: ImageView
    private var isFemaleUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_chat)

        val sharedPreferences = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val fullName = sharedPreferences.getString("full_name", "User")
        isFemaleUser = isFemaleName(fullName ?: "")

        chatContainer = findViewById(R.id.chatContainer)
        chatScrollView = findViewById(R.id.chatScrollView)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Handle Back Button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                addUserMessage(message)
                etMessage.text.clear()
                simulateBotResponse(message)
            }
        }

        // Chip click listeners
        findViewById<android.view.View>(R.id.chipTech).setOnClickListener {
            handleChipClick("Technical Issue")
        }
        findViewById<android.view.View>(R.id.chipAi).setOnClickListener {
            handleChipClick("AI Model Help")
        }
        findViewById<android.view.View>(R.id.chipAccount).setOnClickListener {
            handleChipClick("Account Access")
        }
        findViewById<android.view.View>(R.id.chipBug).setOnClickListener {
            handleChipClick("Bug Report")
        }

        // Add initial bot greeting
        addBotMessage("Welcome to OvaDrugX Support!\nHow can I assist you today?")

        // Handle initial message from Intent if present
        val initialMessage = intent.getStringExtra("EXT_CHAT_MESSAGE")
        if (!initialMessage.isNullOrEmpty()) {
            addUserMessage(initialMessage)
            simulateBotResponse(initialMessage)
        }
    }

    private fun handleChipClick(message: String) {
        addUserMessage(message)
        simulateBotResponse(message)
    }

    private fun addUserMessage(message: String) {
        val view = android.view.LayoutInflater.from(this).inflate(R.layout.item_chat_user, chatContainer, false)
        val tvMessage = view.findViewById<android.widget.TextView>(R.id.tvUserMessage)
        tvMessage.text = message
        
        val ivAvatar = view.findViewById<ImageView>(R.id.ivUserAvatar)
        if (isFemaleUser) {
            ivAvatar.setImageResource(R.drawable.ic_profile_female)
        } else {
            ivAvatar.setImageResource(R.drawable.ic_profile_male)
        }

        chatContainer.addView(view)
        scrollToBottom()
    }

    private fun addBotMessage(message: String) {
        val view = android.view.LayoutInflater.from(this).inflate(R.layout.item_chat_bot, chatContainer, false)
        val tvMessage = view.findViewById<android.widget.TextView>(R.id.tvBotMessage)
        tvMessage.text = message
        chatContainer.addView(view)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        chatScrollView.post {
            chatScrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN)
        }
    }

    private fun simulateBotResponse(userMessage: String) {
        val request = com.simats.ovadrugx.model.SupportChatRequest(userMessage)
        
        com.simats.ovadrugx.api.RetrofitClient.instance.supportChat(request)
            .enqueue(object : retrofit2.Callback<com.simats.ovadrugx.model.SupportChatResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.simats.ovadrugx.model.SupportChatResponse>,
                    response: retrofit2.Response<com.simats.ovadrugx.model.SupportChatResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val reply = response.body()?.reply ?: "I couldn't process that. Could you try again?"
                        addBotMessage(reply)
                    } else {
                        addBotMessage("Sorry, I'm having trouble connecting to the support server. Please try again later.")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<com.simats.ovadrugx.model.SupportChatResponse>,
                    t: Throwable
                ) {
                    addBotMessage("Network Error: ${t.message}. Please check your connection.")
                }
            })
    }
    private fun isFemaleName(fullName: String): Boolean {
        val name = fullName.lowercase()
        if (name.contains("mrs.") || name.contains("ms.") || name.contains("miss")) return true
        if (name.contains("mr.")) return false

        val words = name.replace("dr.", "").trim().split(" ")
        val firstName = words.firstOrNull() ?: return false
        
        val femaleEndings = listOf("a", "i", "ee", "ia", "ya", "na", "ta")
        val maleExceptions = listOf("aditya", "krishna", "shiva", "datta", "chandra", "surya", "chaitanya")

        if (maleExceptions.contains(firstName)) return false
        return femaleEndings.any { firstName.endsWith(it) }
    }
}
