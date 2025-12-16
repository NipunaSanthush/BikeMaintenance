package com.example.bikemaintenance

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
    private lateinit var tvFuelEconomy: TextView

    private var serviceTotal = 0.0
    private var fuelTotal = 0.0

    private val mileageUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == RideTrackingService.ACTION_UPDATE_UI) {
                updateMileageUI()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocation || coarseLocation) {
                startRideService()
            } else {
                Toast.makeText(requireContext(), "Location permission needed for tracking!", Toast.LENGTH_SHORT).show()
            }
        }

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
        tvFuelEconomy = view.findViewById(R.id.tvFuelEconomy)

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

        updateRideButtonState()

        btnRide.setOnClickListener {
            if (session.isTracking()) {
                stopRideService()
            } else {
                checkPermissionsAndStart()
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            showSelectionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        updateMileageUI()

        val filter = IntentFilter(RideTrackingService.ACTION_UPDATE_UI)

        ContextCompat.registerReceiver(
            requireContext(),
            mileageUpdateReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        try {
            requireContext().unregisterReceiver(mileageUpdateReceiver)
        } catch (e: Exception) {
        }
    }

    private fun updateMileageUI() {
        val currentMileage = session.getMileage()
        tvHomeMileage.text = "%.1f km".format(currentMileage)
    }

    private fun checkPermissionsAndStart() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startRideService() {
        val intent = Intent(requireContext(), RideTrackingService::class.java)
        intent.action = RideTrackingService.ACTION_START
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }

        session.setTrackingState(true)
        updateRideButtonState()
        Toast.makeText(requireContext(), "Ride Started! GPS On", Toast.LENGTH_SHORT).show()
    }

    private fun stopRideService() {
        val intent = Intent(requireContext(), RideTrackingService::class.java)
        intent.action = RideTrackingService.ACTION_STOP
        requireContext().startService(intent)

        session.setTrackingState(false)
        updateRideButtonState()
        Toast.makeText(requireContext(), "Ride Stopped. Mileage Saved!", Toast.LENGTH_SHORT).show()
    }

    private fun updateRideButtonState() {
        if (session.isTracking()) {
            btnRide.text = "Stop Ride"
            btnRide.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
            btnRide.setIconResource(R.drawable.ic_close)
        } else {
            btnRide.text = "Start Ride"
            btnRide.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ride_button_start_color))
            btnRide.setIconResource(R.drawable.ic_motorcycle)
        }
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