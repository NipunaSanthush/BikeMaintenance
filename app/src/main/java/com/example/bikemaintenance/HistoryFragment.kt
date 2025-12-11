package com.example.bikemaintenance

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.adapter.FuelAdapter
import com.example.bikemaintenance.adapter.MaintenanceAdapter
import com.example.bikemaintenance.adapter.TripAdapter
import com.example.bikemaintenance.viewmodel.MaintenanceViewModel
import com.example.bikemaintenance.viewmodel.MaintenanceViewModelFactory

class HistoryFragment : Fragment() {

    private val maintenanceViewModel: MaintenanceViewModel by viewModels {
        MaintenanceViewModelFactory((requireActivity().application as BikeApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        val btnServices = view.findViewById<Button>(R.id.btnShowServices)
        val btnFuel = view.findViewById<Button>(R.id.btnShowFuel)
        val btnTrips = view.findViewById<Button>(R.id.btnShowTrips)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val serviceAdapter = MaintenanceAdapter()
        val fuelAdapter = FuelAdapter()
        val tripAdapter = TripAdapter()

        recyclerView.adapter = serviceAdapter

        maintenanceViewModel.allRecords.observe(viewLifecycleOwner) { serviceAdapter.submitList(it) }
        maintenanceViewModel.allFuelRecords.observe(viewLifecycleOwner) { fuelAdapter.submitList(it) }
        maintenanceViewModel.allTrips.observe(viewLifecycleOwner) { tripAdapter.submitList(it) }

        btnServices.setOnClickListener {
            recyclerView.adapter = serviceAdapter
            updateButtonColors(btnServices, btnFuel, btnTrips)
        }

        btnFuel.setOnClickListener {
            recyclerView.adapter = fuelAdapter
            updateButtonColors(btnFuel, btnServices, btnTrips)
        }

        btnTrips.setOnClickListener {
            recyclerView.adapter = tripAdapter
            updateButtonColors(btnTrips, btnServices, btnFuel)
        }
    }

    private fun updateButtonColors(activeBtn: Button, inactive1: Button, inactive2: Button) {
        val activeColor = ColorStateList.valueOf(Color.parseColor("#1565C0"))
        val inactiveColor = ColorStateList.valueOf(Color.parseColor("#B0BEC5"))

        activeBtn.backgroundTintList = activeColor
        inactive1.backgroundTintList = inactiveColor
        inactive2.backgroundTintList = inactiveColor
    }
}