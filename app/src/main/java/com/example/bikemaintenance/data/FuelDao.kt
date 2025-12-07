package com.example.bikemaintenance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelDao{
    @Insert
    suspend fun insertFuelRecord(record: FuelRecord)

    @Query("SELECT * FROM fuel_table ORDER BY id DESC")
    fun getAllFuelRecords(): Flow<List<FuelRecord>>

    @Query("SELECT * FROM fuel_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastFuelRecord(): FuelRecord?
}