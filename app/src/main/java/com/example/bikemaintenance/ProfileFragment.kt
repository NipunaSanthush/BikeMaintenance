package com.example.bikemaintenance

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.bikemaintenance.utils.SessionManager

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var session: SessionManager

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imgProfile.setImageURI(uri)

            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(uri, flag)

            session.saveProfileImage(uri.toString())
            Toast.makeText(requireContext(), "Profile Photo Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        val userDetails = session.getUserDetails()

        imgProfile = view.findViewById(R.id.imgProfile)
        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvBike = view.findViewById<TextView>(R.id.tvProfileBikeModel)
        val tvPlate = view.findViewById<TextView>(R.id.tvProfilePlate)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val btnSettings = view.findViewById<View>(R.id.btnSettings)

        val savedImage = session.getProfileImage()
        if (savedImage != null) {
            imgProfile.setImageURI(Uri.parse(savedImage))
        }

        imgProfile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

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