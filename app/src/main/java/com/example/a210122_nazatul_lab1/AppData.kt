package com.example.a210122_nazatul_lab1

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "appliances")
data class ApplianceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val name: String,
    val wattage: Double,
    val hoursPerDay: Double,
    val monthlyKwh: Double
)

@Entity(tableName = "bill_goals")
data class BillGoalEntity(
    @PrimaryKey val username: String,
    val goalRm: Double
)


data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int? = null
)


data class ApplianceItem(
    val id: Int,
    val name: String,
    val wattage: Double,
    val hoursPerDay: Double,
    val estimatedMonthlyCost: Double,
    val monthlyKwh: Double
)


fun ApplianceEntity.toItem() = ApplianceItem(
    id = id,
    name = name,
    wattage = wattage,
    hoursPerDay = hoursPerDay,
    estimatedMonthlyCost = 0.0,
    monthlyKwh = monthlyKwh
)