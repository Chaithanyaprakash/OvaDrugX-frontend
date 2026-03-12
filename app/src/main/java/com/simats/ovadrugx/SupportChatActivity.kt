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
        val lowerMsg = userMessage.lowercase().trim()
        
        // Language detection - check for scripts or specific keywords
        val isHindi = lowerMsg.any { it in '\u0900'..'\u097F' }
        val isTelugu = lowerMsg.any { it in '\u0C00'..'\u0C7F' }
        val isTamil = lowerMsg.any { it in '\u0B80'..'\u0BFF' }
        
        val lang = when {
            isHindi -> "hi"
            isTelugu -> "te"
            isTamil -> "ta"
            else -> "en"
        }

        val projectKeywords = mapOf(
            "en" to listOf("drug", "screening", "gene", "expression", "protein", "compound", "chemical", "molecular", "docking", "target", "insight", "discovery", "trial"),
            "hi" to listOf("दवा", "जीन", "प्रोटीन", "जाँच", "अणु", "यौगिक", "नतीजा", "खोज"),
            "te" to listOf("మందు", "జన్యువు", "ప్రోటీన్", "స్క్రీనింగ్", "అణువు", "సమ్మేళనం", "ఫలితం", "పరిశోధన"),
            "ta" to listOf("மருந்து", "மரபணு", "புரதம்", "திரையிடல்", "மூலக்கூறு", "கலவை", "முடிவு", "ஆராய்ச்சி")
        )

        val technicalKeywords = mapOf(
            "en" to listOf("bug", "error", "crash", "login", "password", "access", "account", "upload", "file", "network", "sync", "failed"),
            "hi" to listOf("समस्या", "त्रुटि", "लॉगइन", "पासवर्ड", "अकाउंट", "फाइल", "तकनीकी", "नेटवर्क", "विफल"),
            "te" to listOf("సమస్య", "లోపం", "లాగిన్", "పాస్‌వర్డ్", "ఖాతా", "ఫైల్", "సాంకేతిక", "నెట్‌వర్క్", "విఫలం"),
            "ta" to listOf("பிரச்சனை", "பிழை", "லாகின்", "கடவுச்சொல்", "கணக்கு", "கோப்பு", "தொழில்நுட்ப", "நெட்வொர்க்", "தோல்வி")
        )

        val isProjectRelated = projectKeywords[lang]?.any { lowerMsg.contains(it) } ?: false
        val isTechnicalIssue = technicalKeywords[lang]?.any { lowerMsg.contains(it) } ?: false
        val isGreeting = when(lang) {
            "hi" -> lowerMsg.contains("नमस्ते") || lowerMsg.contains("हेलो") || lowerMsg.contains("नमस्ते")
            "te" -> lowerMsg.contains("నమస్కారం") || lowerMsg.contains("హలో")
            "ta" -> lowerMsg.contains("வணக்கம்") || lowerMsg.contains("ஹலோ")
            else -> lowerMsg.matches(Regex(".*\\b(hi|hello|hey|greetings|help)\\b.*"))
        }

        val response = when (lang) {
            "hi" -> when {
                isTechnicalIssue -> "मैंने आपकी तकनीकी रिपोर्ट नोट कर ली है। हमारी टीम इसकी जांच कर रही है।"
                isProjectRelated -> "यह ओवाड्रगएक्स शोध का एक महत्वपूर्ण हिस्सा है। हमारे मॉडल इन अणुओं का विश्लेषण करते हैं।"
                isGreeting -> "नमस्ते! मैं ओवाड्रगएक्स सहायक हूँ। मैं दवा, जीन, या तकनीकी समस्याओं में मदद कर सकता हूँ।"
                else -> "मैं ओवाड्रगएक्स प्रोजेक्ट विषयों और तकनीकी समस्याओं में मदद करता हूँ। क्या आप स्पष्ट कर सकते हैं?"
            }
            "te" -> when {
                isTechnicalIssue -> "నేను మీ సాంకేతిక సమస్యను గమనించాను. మా బృందం దీనిపై పని చేస్తోంది."
                isProjectRelated -> "ఇది OvaDrugX పరిశోధనలో కీలక భాగం. మా మోడల్స్ ఈ జన్యువులను విశ్లేషిస్తాయి."
                isGreeting -> "నమస్కారం! నేను OvaDrugX సహాయకుడిని. మందులు లేదా సాంకేతిక సమస్యల గురించి అడగండి."
                else -> "నేను OvaDrugX ప్రాజెక్ట్ మరియు సాంకేతిక సమస్యలలో సహాయం చేయగలను. దయచేసి వివరంగా చెప్పండి."
            }
            "ta" -> when {
                isTechnicalIssue -> "உங்கள் தொழில்முறை சிக்கலை நான் கவனித்தேன். எங்கள் குழு இதை ஆய்வு செய்கிறது."
                isProjectRelated -> "இது OvaDrugX ஆராய்ச்சியின் ஒரு முக்கிய பகுதியாகும். எங்கள் AI மாதிரிகள் இதை ஆய்வு செய்கின்றன."
                isGreeting -> "வணக்கம்! நான் OvaDrugX உதவியாளர். மருந்து அல்லது தொழில்நுட்ப சிக்கல்கள் குறித்து கேளுங்கள்."
                else -> "OvaDrugX திட்ட தலைப்புகள் மற்றும் தொழில்நுட்ப சிக்கல்களில் நான் உதவ முடியும். தயவுசெய்து விளக்கவும்."
            }
            else -> when {
                isTechnicalIssue -> "I've noted your report regarding a technical issue. Our dev team is investigating it to ensure OvaDrugX runs smoothly."
                isProjectRelated -> "That's a vital part of our research. Our AI models analyze those specific molecular signatures to find effective drug candidates."
                isGreeting -> "Hello! I am the OvaDrugX AI assistant. I can help with drugs, genes, or technical issues."
                else -> "I specialize in OvaDrugX project topics and technical issues. Could you please clarify your request?"
            }
        }

        val suffix = when(lang) {
            "hi" -> "क्या मैं कुछ और मदद कर सकता हूँ?"
            "te" -> "నేను ఇంకా ఏదైనా సహాయం చేయగలనా?"
            "ta" -> "நான் வேறு ஏதேனும் உதவி செய்ய வேண்டுமா?"
            else -> "Is there anything specific I can help with?"
        }

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            addBotMessage("$response\n\n$suffix")
        }, 1000)
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
