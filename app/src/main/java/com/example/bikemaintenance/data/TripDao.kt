package com.example.bikemaintenance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: TripRecord)

    @Query("SELECT * FROM trip_table ORDER BY id DESC")
    fun getAllTrips(): Flow<List<TripRecord>>
}