package com.example.bikemaintenance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.bikemaintenance.utils.SessionManager

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        val userDetails = session.getUserDetails()

        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvBike = view.findViewById<TextView>(R.id.tvProfileBikeModel)
        val tvPlate = view.findViewById<TextView>(R.id.tvProfilePlate)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnSettings = view.findViewById<View>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        tvName.text = userDetails[SessionManager.KEY_NAME]
        tvBike.text = userDetails[SessionManager.KEY_BIKE_MODEL]
        tvPlate.text = userDetails[SessionManager.KEY_LICENSE_PLATE]

        btnLogout.setOnClickListener {
            session.logoutUser()

            val intent = Intent(requireContext(), SetupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}