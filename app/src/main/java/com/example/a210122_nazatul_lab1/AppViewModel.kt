package com.example.a210122_nazatul_lab1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ------------------------------------------------------------------
// TNB Bill calculation — unchanged from Lab 4
// ------------------------------------------------------------------
fun calculateTnbBill(monthlyKwh: Double): Double {
    if (monthlyKwh <= 0.0) return 0.0
    val ratePerKwh = 0.454
    var bill = monthlyKwh * ratePerKwh
    if (monthlyKwh > 600) bill += 10.0
    if (monthlyKwh <= 1000) {
        var rebate = 0.0
        val t1 = minOf(monthlyKwh, 200.0)
        rebate += t1 * 0.25
        if (monthlyKwh > 200) rebate += minOf(monthlyKwh - 200, 200.0) * 0.15
        if (monthlyKwh > 400) rebate += minOf(monthlyKwh - 400, 200.0) * 0.10
        if (monthlyKwh > 600) rebate += minOf(monthlyKwh - 600, 400.0) * 0.02
        bill -= rebate
    }
    return maxOf(bill, 0.0)
}

@OptIn(ExperimentalCoroutinesApi::class)
class SmartTenagaViewModel(
    private val repository: ApplianceRepository
) : ViewModel() {

    var loggedInUsername by mutableStateOf("")
        private set

    fun login(username: String) {
        loggedInUsername = username.trim()
    }

    fun logout() {
        loggedInUsername = ""
    }

    // Appliance list — switches automatically when username changes
    val applianceEntityList: StateFlow<List<ApplianceEntity>> =
        snapshotFlow { loggedInUsername }
            .flatMapLatest { username ->
                if (username.isBlank()) flowOf(emptyList())
                else repository.getAppliancesForUser(username)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val applianceList: StateFlow<List<ApplianceItem>> =
        applianceEntityList
            .map { entities -> entities.map { it.toItem() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // Bill goal — switches automatically when username changes
    private val billGoalEntity: StateFlow<BillGoalEntity?> =
        snapshotFlow { loggedInUsername }
            .flatMapLatest { username ->
                if (username.isBlank()) flowOf(null)
                else repository.getGoalForUser(username)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    val billGoalRm: StateFlow<Double> =
        billGoalEntity
            .map { it?.goalRm ?: 0.0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )

    // Computed properties
    val totalMonthlyKwh: Double
        get() = applianceList.value.sumOf { it.monthlyKwh }

    val totalEstimatedCost: Double
        get() = calculateTnbBill(totalMonthlyKwh)

    fun applianceProportionalCost(appliance: ApplianceItem): Double {
        if (totalMonthlyKwh <= 0) return 0.0
        return totalEstimatedCost * (appliance.monthlyKwh / totalMonthlyKwh)
    }

    val goalProgress: Float
        get() {
            val goal = billGoalRm.value
            return if (goal > 0) (totalEstimatedCost / goal).toFloat().coerceAtMost(1f) else 0f
        }

    val isOverGoal: Boolean
        get() = billGoalRm.value > 0 && totalEstimatedCost > billGoalRm.value

    // Write operations
    fun addAppliance(name: String, wattage: Double, hoursPerDay: Double) {
        val monthlyKwh = (wattage * hoursPerDay * 30) / 1000.0
        viewModelScope.launch {
            repository.addAppliance(
                ApplianceEntity(
                    username    = loggedInUsername,
                    name        = name.trim(),
                    wattage     = wattage,
                    hoursPerDay = hoursPerDay,
                    monthlyKwh  = monthlyKwh
                )
            )
        }
    }

    fun deleteAppliance(id: Int) {
        val entity = applianceEntityList.value.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repository.deleteAppliance(entity)
        }
    }

    fun setBillGoal(rm: Double) {
        viewModelScope.launch {
            repository.setBillGoal(loggedInUsername, rm)
        }
    }

    val notifications = listOf(
        NotificationItem(1, "Save Energy & Reduce Carbon", "Simple steps for a greener planet: Switch off lights, use energy-efficient lighting, and more.", R.drawable.savenergy),
        NotificationItem(2, "Peak Hour Awareness", "Learn about peak hours and how to save energy during high demand periods.", R.drawable.peakhour),
        NotificationItem(3, "Smart Appliance Monitor", "TNB's new smart meter rollout...", null),
        NotificationItem(4, "Carbon Emission Tracker", "Malaysia's electricity carbon...", null),
        NotificationItem(5, "Energy Saving Gamification", "A new national initiative...", null)
    )
}

class SmartTenagaViewModelFactory(
    private val repository: ApplianceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmartTenagaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmartTenagaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}