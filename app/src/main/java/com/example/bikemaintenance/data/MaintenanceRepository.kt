package com.example.bikemaintenance.data

import kotlinx.coroutines.flow.Flow

class MaintenanceRepository(
    private val maintenanceDao: MaintenanceDao,
    private val fuelDao: FuelDao
){

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

    val allFuelRecords: Flow<List<FuelRecord>> = fuelDao.getAllFuelRecords()

    suspend fun insertFuel(record: FuelRecord){
        fuelDao.insertFuelRecord(record)
    }
}