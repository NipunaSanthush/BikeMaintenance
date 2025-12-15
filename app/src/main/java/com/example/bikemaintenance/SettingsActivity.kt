package com.example.bikemaintenance

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
    }

    private fun savePreference(key: String, value: Boolean) {
        val sharedPrefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
}