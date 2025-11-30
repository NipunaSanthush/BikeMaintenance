package com.example.bikemaintenance.data

import kotlinx.coroutines.flow.Flow

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao){

    val allRecords: Flow<List<MaintenanceRecord>> = maintenanceDao.getAllRecords()

    suspend fun insert(record: MaintenanceRecord){
        maintenanceDao.insertRecord(record)
    }

    suspend fun update(record: MaintenanceRecord){
        maintenanceDao.updateRecord(record)
    }

    suspend fun delete(record: MaintenanceRecord){
        maintenanceDao.deleteRecord(record)
    }
}