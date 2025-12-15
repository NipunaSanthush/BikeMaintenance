package com.example.bikemaintenance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bikemaintenance.adapter.MaintenanceAdapter
import com.example.bikemaintenance.data.FuelRecord
import com.example.bikemaintenance.data.MaintenanceRecord
import com.example.bikemaintenance.utils.SessionManager
import com.example.bikemaintenance.viewmodel.MaintenanceViewModel
import com.example.bikemaintenance.viewmodel.MaintenanceViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private val maintenanceViewModel: MaintenanceViewModel by viewModels {
        MaintenanceViewModelFactory((requireActivity().application as BikeApplication).repository)
    }

    private lateinit var tvTotalCost: TextView
    private lateinit var tvHomeMileage: TextView
    private lateinit var session: SessionManager
    private lateinit var btnRide: MaterialButton

    private var serviceTotal = 0.0
    private var fuelTotal = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val userDetails = session.getUserDetails()
        val name = userDetails[SessionManager.KEY_NAME]

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Welcome, $name!"

        tvHomeMileage = view.findViewById(R.id.tvHomeMileage)
        btnRide = view.findViewById(R.id.btnRideToggle)
        tvTotalCost = view.findViewById(R.id.tvTotalCost)
        val tvFuelEconomy = view.findViewById<TextView>(R.id.tvFuelEconomy)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = MaintenanceAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        maintenanceViewModel.allRecords.observe(viewLifecycleOwner) { records ->
            records?.let {
                adapter.submitList(it)
                var total = 0.0
                for (record in it) {
                    total += record.cost
                }
                serviceTotal = total
                updateTotalCost()
            }
        }

        maintenanceViewModel.allFuelRecords.observe(viewLifecycleOwner){ fuelList ->
            if (fuelList != null && fuelList.size >= 2) {
                val sortedList = fuelList.sortedBy { it.odometer }

                val lastRecord = sortedList.last()
                val prevRecord = sortedList[sortedList.size - 2]

                val distance = lastRecord.odometer - prevRecord.odometer

                val mileage = distance / lastRecord.liters

                tvFuelEconomy.text = "%.1f km/L".format(mileage)
            }else{
                tvFuelEconomy.text = "- km/L"
            }

            var total = 0.0
            if (fuelList != null) {
                for (record in fuelList) {
                    total += record.cost
                }
            }
            fuelTotal = total
            updateTotalCost()
        }

        btnRide.setOnClickListener {
            if (btnRide.text == "Ride") {
                btnRide.text = "Stop"
                btnRide.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            } else {
                btnRide.text = "Ride"
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            showSelectionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentMileage = session.getMileage()
        tvHomeMileage.text = "$currentMileage km"
    }

    private fun updateTotalCost() {
        val overallTotal = serviceTotal + fuelTotal
        tvTotalCost.text = "Rs. %.2f".format(overallTotal)
    }

    private fun showSelectionDialog() {
        val options = arrayOf("Add Service Record", "Add Fuel Record")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("What do you want to add?")
        builder.setItems(options) { _, which ->
            if (which == 0) {
                showAddServiceDialog()
            } else {
                showAddFuelDialog()
            }
        }
        builder.show()
    }

    private fun showAddServiceDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Service Record")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_maintenance, null)
        builder.setView(view)

        val etType = view.findViewById<EditText>(R.id.etServiceType)
        val etMileage = view.findViewById<EditText>(R.id.etMileage)
        val etCost = view.findViewById<EditText>(R.id.etCost)
        val etDate = view.findViewById<EditText>(R.id.etDate)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(currentDate)

        builder.setPositiveButton("Save") { _, _ ->
            val type = etType.text.toString()
            val mileageStr = etMileage.text.toString()
            val costStr = etCost.text.toString()
            val date = etDate.text.toString()

            if (type.isNotEmpty() && mileageStr.isNotEmpty() && costStr.isNotEmpty()) {
                val record = MaintenanceRecord(
                    serviceType = type,
                    mileage = mileageStr.toInt(),
                    cost = costStr.toDouble(),
                    date = date
                )
                maintenanceViewModel.insert(record)
                Toast.makeText(requireContext(), "Service Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showAddFuelDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Fuel Record")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_fuel, null)
        builder.setView(view)

        val etDate = view.findViewById<EditText>(R.id.etFuelDate)
        val etLiters = view.findViewById<EditText>(R.id.etFuelLiters)
        val etCost = view.findViewById<EditText>(R.id.etFuelCost)
        val etOdometer = view.findViewById<EditText>(R.id.etFuelOdometer)

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(currentDate)

        builder.setPositiveButton("Save") { _, _ ->
            val date = etDate.text.toString()
            val litersStr = etLiters.text.toString()
            val costStr = etCost.text.toString()
            val odoStr = etOdometer.text.toString()

            if (date.isNotEmpty() && litersStr.isNotEmpty() && costStr.isNotEmpty() && odoStr.isNotEmpty()) {
                val record = FuelRecord(
                    date = date,
                    liters = litersStr.toDouble(),
                    cost = costStr.toDouble(),
                    odometer = odoStr.toInt()
                )
                maintenanceViewModel.insertFuel(record)
                Toast.makeText(requireContext(), "Fuel Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}