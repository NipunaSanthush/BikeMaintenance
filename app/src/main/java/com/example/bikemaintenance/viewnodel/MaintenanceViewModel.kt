package com.example.bikemaintenance.viewnodel

import androidx.lifecycle.*
import com.example.bikemaintenance.data.MaintenanceRecord
import com.example.bikemaintenance.data.MaintenanceRepository
import kotlinx.coroutines.launch

class MaintenanceViewModel(private val repository: MaintenanceRepository) : ViewModel(){

    val allRecords: LiveData<List<MaintenanceRecord>> = repository.allRecords.asLiveData()

    fun insert(record: MaintenanceRecord) = viewModelScope.launch{
        repository.insert(record)
    }

    fun update(record: MaintenanceRecord) = viewModelScope.launch{
        repository.update(record)
    }

    fun delete(record: MaintenanceRecord) = viewModelScope.launch{
        repository.delete(record)
    }
}

class MaintenanceViewModelFactory(private val repository: MaintenanceRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaintenanceViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MaintenanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}