package com.simats.ovadrugx.api

import com.simats.ovadrugx.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("verify")
    fun verify(@Body request: VerifyRequest): Call<GenericResponse>

    @POST("forgot-password")
    fun forgotPassword(@Body request: ForgotPasswordRequest): Call<RegisterResponse>

    @POST("verify-reset-otp")
    fun verifyResetOtp(@Body request: VerifyResetOtpRequest): Call<GenericResponse>

    @POST("reset-password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<GenericResponse>

    @POST("predict-targets")
    fun predictTargets(@Body request: PredictTargetsRequest): Call<PredictTargetsResponse>

    @POST("screen-drug")
    fun screenDrug(@Body request: ScreenDrugRequest): Call<ScreenDrugResponse>

    @POST("get-account")
    fun getAccount(@Body request: GetAccountRequest): Call<GetAccountResponse>

    @POST("update-account")
    fun updateAccount(@Body request: UpdateAccountRequest): Call<GenericResponse>

    // --- Future AI Data Endpoints Placeholder ---

    /**
     * Placeholder backend endpoint to fetch live real-time AI docking scores.
     */
    @retrofit2.http.GET("ai/docking_scores.php")
    fun getLiveDockingScores(
        @retrofit2.http.Query("target_id") targetId: String
    ): Call<Any> // Call<AiDockingResponse>

    /**
     * Placeholder backend endpoint to fetch the latest AI model accuracy metrics.
     */
    @retrofit2.http.GET("ai/model_metrics.php")
    fun getModelMetrics(): Call<Any> // Call<AiMetricsResponse>
}
