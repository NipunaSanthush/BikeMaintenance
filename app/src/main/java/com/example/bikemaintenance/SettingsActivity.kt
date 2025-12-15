package com.example.bikemaintenance

import android.content.Context
import android.os.Bundle
import android.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.bikemaintenance.utils.SessionManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var tvMileageSubtitle: TextView
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        session = SessionManager(this)

        val switchDark = findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchNotif = findViewById<SwitchMaterial>(R.id.switchNotifications)

        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DARK_MODE", false)
        val isNotifOn = sharedPrefs.getBoolean("NOTIFICATIONS", true)

        switchDark.isChecked = isDarkMode
        switchNotif.isChecked = isNotifOn

        switchDark.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    savePreference("DARK_MODE", true)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    savePreference("DARK_MODE", false)
                }
            }
        }

        switchNotif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                savePreference("NOTIFICATIONS", isChecked)
            }
        }

        val cardMileage = findViewById<CardView>(R.id.cardMileage)
        tvMileageSubtitle = findViewById(R.id.tvCurrentMileageSetting)

        updateMileageDisplay()

        cardMileage.setOnClickListener {
            showEditMileageDialog()
        }
    }

    private fun updateMileageDisplay() {
        val mileage = session.getMileage()
        tvMileageSubtitle.text = "$mileage km"
    }

    private fun showEditMileageDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Odometer")
        builder.setMessage("Enter current odometer reading (km)")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(session.getMileage().toString())

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(50, 0, 50, 0)
        input.layoutParams = params
        container.addView(input)

        builder.setView(container)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newMileageString = input.text.toString()
            if (newMileageString.isNotEmpty()) {
                val newMileage = newMileageString.toFloat()
                session.saveMileage(newMileage)
                updateMileageDisplay()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun savePreference(key: String, value: Boolean) {
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
}