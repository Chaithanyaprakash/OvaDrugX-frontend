package com.simats.ovadrugx.model

import kotlinx.serialization.Serializable

@Serializable
data class SupportChatRequest(
    val message: String
)

@Serializable
data class SupportChatResponse(
    val status: String? = null,
    val message: String? = null,
    val reply: String? = null
)
