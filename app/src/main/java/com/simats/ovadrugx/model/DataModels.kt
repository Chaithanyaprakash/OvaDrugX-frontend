package com.simats.ovadrugx.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserData(
    val email: String,
    @SerialName("full_name") val fullName: String? = null,
    val mobile: String? = null
)

@Serializable
data class LoginResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null,
    val data: UserData? = null
)

@Serializable
data class RegisterRequest(
    @SerialName("full_name") val fullName: String,
    val email: String,
    val mobile: String,
    val password: String,
    @SerialName("confirm_password") val confirmPassword: String
)

@Serializable
data class RegisterResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class GenericResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class VerifyRequest(
    val email: String,
    val otp: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class VerifyResetOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    @SerialName("new_password") val newPassword: String
)

@Serializable
data class PredictTargetsRequest(
    @SerialName("gene_name") val geneName: String
)

@Serializable
data class PredictTargetsData(
    @SerialName("input_gene") val inputGene: String? = null,
    @SerialName("matched_gene") val matchedGene: String? = null,
    val organism: String? = null,
    @SerialName("confidence_score") val confidenceScore: Double? = null,
    @SerialName("high_confidence_targets") val highConfidenceTargets: List<String>? = null
)

@Serializable
data class PredictTargetsResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null,
    val data: PredictTargetsData? = null
)

@Serializable
data class ScreenDrugRequest(
    val gene: String,
    val mirna: String,
    val compound: String
)

@Serializable
data class ScreenDrugData(
    @SerialName("overall_match_score") val overallMatchScore: Double? = null,
    @SerialName("gene_confidence") val geneConfidence: Double? = null,
    @SerialName("mirna_confidence") val mirnaConfidence: Double? = null,
    @SerialName("compound_affinity") val compoundAffinity: Double? = null,
    @SerialName("suitability_rating") val suitabilityRating: String? = null,
    @SerialName("recommended_drug") val recommendedDrug: String? = null
)

@Serializable
data class ScreenDrugResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null,
    val data: ScreenDrugData? = null
)
