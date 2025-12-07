package com.example.bikemaintenance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_table")
data class FuelRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val liters: Double,
    val cost: Double,
    val odometer: Int
)