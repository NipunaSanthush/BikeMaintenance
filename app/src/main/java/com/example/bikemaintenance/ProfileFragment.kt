package com.example.bikemaintenance

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.bikemaintenance.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yalantis.ucrop.UCrop
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var session: SessionManager

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            startCrop(uri)
        }
    }

    private val cropActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                saveAndShowImage(resultUri)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Crop Error!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = SessionManager(requireContext())
        imgProfile = view.findViewById(R.id.imgProfile)
        val fabEdit = view.findViewById<FloatingActionButton>(R.id.fabEditProfile)

        loadProfileImage()

        fabEdit.setOnClickListener {
            showProfileOptions()
        }

        imgProfile.setOnClickListener {
            val savedImage = session.getProfileImage()
            if (savedImage != null) {
                showFullImage(Uri.parse(savedImage))
            } else {
                showProfileOptions()
            }
        }

        val userDetails = session.getUserDetails()
        view.findViewById<TextView>(R.id.tvProfileName).text = userDetails[SessionManager.KEY_NAME]

        view.findViewById<TextView>(R.id.tvProfileBikeModel).text = userDetails[SessionManager.KEY_BIKE_MODEL]
        view.findViewById<TextView>(R.id.tvProfilePlate).text = userDetails[SessionManager.KEY_LICENSE_PLATE]

        view.findViewById<View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            session.logoutUser()
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showProfileOptions() {
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_profile_options, null)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.layoutChangePhoto).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.layoutRemovePhoto).setOnClickListener {
            session.removeProfileImage()
            imgProfile.setImageResource(R.drawable.ic_profile)
            Toast.makeText(requireContext(), "Photo Removed", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startCrop(uri: Uri) {
        val destinationFileName = "cropped_profile_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, destinationFileName))

        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        options.setCompressionQuality(80)

        try {
            options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.brand_primary))
            options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.brand_primary_variant))
        } catch (e: Exception) {
            options.setToolbarColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            options.setStatusBarColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
        }

        val uCrop = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        val intent = uCrop.getIntent(requireContext())
        cropActivityLauncher.launch(intent)
    }

    private fun saveAndShowImage(uri: Uri) {
        session.saveProfileImage(uri.toString())

        Glide.with(this)
            .load(uri)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imgProfile)
    }

    private fun loadProfileImage() {
        val savedImage = session.getProfileImage()
        if (savedImage != null) {
            Glide.with(this)
                .load(Uri.parse(savedImage))
                .circleCrop()
                .placeholder(R.drawable.ic_profile)
                .into(imgProfile)
        }
    }

    private fun showFullImage(uri: Uri) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_full_image)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val fullImg = dialog.findViewById<ImageView>(R.id.imgFullProfile)
        Glide.with(this).load(uri).into(fullImg)

        dialog.findViewById<View>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}