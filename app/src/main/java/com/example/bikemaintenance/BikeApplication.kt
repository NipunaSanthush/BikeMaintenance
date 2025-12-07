package com.example.bikemaintenance

import android.app.Application
import com.example.bikemaintenance.data.BikeDatabase
import com.example.bikemaintenance.data.MaintenanceRepository

class BikeApplication : Application(){

    val database by lazy { BikeDatabase.getDatabase(this) }

    val repository by lazy {
        MaintenanceRepository(
            database.maintenanceDao(),
            database.fuelDao()
        )
    }
}