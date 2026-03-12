package com.simats.ovadrugx.model

data class AccountData(
    val full_name: String,
    val gender: String,
    val department: String,
    val license_number: String
)

data class GetAccountRequest(
    val email: String
)

data class GetAccountResponse(
    val status: String,
    val message: String?,
    val data: AccountData?
)

data class UpdateAccountRequest(
    val email: String,
    val full_name: String,
    val gender: String,
    val department: String,
    val license_number: String
)
