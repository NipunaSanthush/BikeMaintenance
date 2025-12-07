package com.example.bikemaintenance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MaintenanceRecord::class, FuelRecord::class], version = 2, exportSchema = false)
abstract class BikeDatabase : RoomDatabase(){

    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun fuelDao(): FuelDao

    companion object{
        @Volatile
        private var INSTANCE: BikeDatabase? = null

        fun getDatabase(context: Context): BikeDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BikeDatabase::class.java,
                    "bike_maintenance_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}