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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val serviceAdapter = MaintenanceAdapter()
        val fuelAdapter = FuelAdapter()

        recyclerView.adapter = serviceAdapter

        maintenanceViewModel.allRecords.observe(viewLifecycleOwner) { records ->
            serviceAdapter.submitList(records)
        }

        maintenanceViewModel.allFuelRecords.observe(viewLifecycleOwner) { records ->
            fuelAdapter.submitList(records)
        }

        btnServices.setOnClickListener {
            recyclerView.adapter = serviceAdapter

            btnServices.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1565C0"))
            btnFuel.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B0BEC5"))
        }

        btnFuel.setOnClickListener {
            recyclerView.adapter = fuelAdapter

            btnFuel.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1565C0"))
            btnServices.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B0BEC5"))
        }
    }
}