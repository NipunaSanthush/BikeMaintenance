package com.example.bikemaintenance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val adapter = MaintenanceAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        maintenanceViewModel.allRecords.observe(viewLifecycleOwner) { records ->
            records?.let {
                adapter.submitList(it)
            }
        }
    }
}