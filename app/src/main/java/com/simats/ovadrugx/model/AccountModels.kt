package com.simats.ovadrugx.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountData(
    val full_name: String,
    val gender: String,
    val department: String,
    val license_number: String,
    val mobile: String? = null
)

@Serializable
data class GetAccountRequest(
    val email: String
)

@Serializable
data class GetAccountResponse(
    val status: String,
    val message: String?,
    val data: AccountData?
)

@Serializable
data class UpdateAccountRequest(
    val email: String,
    val full_name: String,
    val gender: String,
    val department: String,
    val license_number: String,
    val mobile: String? = null
)
