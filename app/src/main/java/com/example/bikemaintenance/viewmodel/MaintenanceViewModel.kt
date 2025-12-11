package com.example.bikemaintenance.viewmodel

import androidx.lifecycle.*
import com.example.bikemaintenance.data.FuelRecord
import com.example.bikemaintenance.data.MaintenanceRecord
import com.example.bikemaintenance.data.MaintenanceRepository
import com.example.bikemaintenance.data.TripRecord
import kotlinx.coroutines.launch

class MaintenanceViewModel(private val repository: MaintenanceRepository) : ViewModel() {

    val allRecords: LiveData<List<MaintenanceRecord>> = repository.allRecords.asLiveData()

    fun insert(record: MaintenanceRecord) = viewModelScope.launch {
        repository.insert(record)
    }

    val allFuelRecords: LiveData<List<FuelRecord>> = repository.allFuelRecords.asLiveData()

    fun insertFuel(record: FuelRecord) = viewModelScope.launch {
        repository.insertFuel(record)
    }

    val allTrips: LiveData<List<TripRecord>> = repository.allTrips.asLiveData()

    fun insertTrip(trip: TripRecord) = viewModelScope.launch {
        repository.insertTrip(trip)
    }
}

class MaintenanceViewModelFactory(private val repository: MaintenanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaintenanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaintenanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}