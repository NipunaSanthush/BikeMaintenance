package com.example.bikemaintenance.data

import android.renderscript.Type
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "maintenance_table")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceType: String,
    val mileage: Int,
    val date: String,
    val cost: Double,
    val notes: String? = null
)