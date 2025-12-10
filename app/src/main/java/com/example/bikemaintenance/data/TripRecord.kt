package com.example.bikemaintenance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_table")
data class TripRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val distance: String,
    val duration: String,
    val avgSpeed: String
)