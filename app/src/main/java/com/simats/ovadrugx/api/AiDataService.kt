package com.simats.ovadrugx.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * A mock service that simulates receiving live AI data from the backend.
 * Uses Kotlin Coroutines Flow to emit real-time updates.
 */
object AiDataService {

    // Simulating Live Values
    private val _virtualDockingScore = MutableStateFlow(85)
    val virtualDockingScore: StateFlow<Int> = _virtualDockingScore

    private val _aiDiscoveriesCount = MutableStateFlow(24)
    val aiDiscoveriesCount: StateFlow<Int> = _aiDiscoveriesCount

    private val _modelAccuracy = MutableStateFlow(96)
    val modelAccuracy: StateFlow<Int> = _modelAccuracy

    private val _tp53Score = MutableStateFlow(90)
    val tp53Score: StateFlow<Int> = _tp53Score

    private val _brca1Score = MutableStateFlow(82)
    val brca1Score: StateFlow<Int> = _brca1Score

    private val _parp1Score = MutableStateFlow(78)
    val parp1Score: StateFlow<Int> = _parp1Score

    // A coroutine scope tied to the entire application lifecycle
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isSimulating = false

    fun startLiveSimulation() {
        if (isSimulating) return
        isSimulating = true

        scope.launch {
            while (true) {
                // Simulate a delay of 3 seconds between "backend pushes"
                delay(3000)
                
                // Randomly fluctuate values slightly to simulate live processing
                _virtualDockingScore.value = fluctuateValue(_virtualDockingScore.value, 70, 99)
                
                // Discoveries occasionally tick up
                if (Random.nextInt(100) > 85) {
                    _aiDiscoveriesCount.value += 1
                }
                
                _modelAccuracy.value = fluctuateValue(_modelAccuracy.value, 90, 99)
                
                _tp53Score.value = fluctuateValue(_tp53Score.value, 85, 98)
                _brca1Score.value = fluctuateValue(_brca1Score.value, 75, 95)
                _parp1Score.value = fluctuateValue(_parp1Score.value, 70, 90)
            }
        }
    }

    private fun fluctuateValue(current: Int, min: Int, max: Int): Int {
        val change = Random.nextInt(-3, 4) // Fluctuate between -3 and +3
        var newValue = current + change
        if (newValue < min) newValue = min
        if (newValue > max) newValue = max
        return newValue
    }

    /*
     * TODO: Future Retrofit Endpoint Integrations
     * fun fetchRealAiPrediction(targetId: String): Call<AiPredictionResponse> {
     *     return RetrofitClient.apiService.getLivePrediction(targetId)
     * }
     */
}
