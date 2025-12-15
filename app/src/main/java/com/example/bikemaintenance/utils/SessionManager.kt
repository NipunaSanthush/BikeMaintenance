package com.example.bikemaintenance.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object{
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_NAME = "name"
        const val KEY_BIKE_MODEL = "bikeModel"
        const val KEY_LICENSE_PLATE = "licensePlate"
        const val KEY_PROFILE_IMAGE = "profile_image"
        const val KEY_CURRENT_MILEAGE = "current_mileage"
    }

    fun createLoginSession(name: String, bikeModel: String, licensePlate: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_BIKE_MODEL, bikeModel)
        editor.putString(KEY_LICENSE_PLATE, licensePlate)
        editor.apply()
    }

    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user[KEY_NAME] = prefs.getString(KEY_NAME, "User")!!
        user[KEY_BIKE_MODEL] = prefs.getString(KEY_BIKE_MODEL, "Bike")!!
        user[KEY_LICENSE_PLATE] = prefs.getString(KEY_LICENSE_PLATE, "")!!
        return user
    }

    fun isLoggedIn(): Boolean{
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logoutUser() {
        editor.clear()
        editor.commit()
    }

    fun saveProfileImage(uri: String) {
        editor.putString(KEY_PROFILE_IMAGE, uri)
        editor.apply()
    }

    fun getProfileImage(): String? {
        return prefs.getString(KEY_PROFILE_IMAGE, null)
    }

    fun removeProfileImage() {
        editor.remove(KEY_PROFILE_IMAGE)
        editor.apply()
    }

    fun saveMileage(km: Float){
        editor.putFloat(KEY_CURRENT_MILEAGE, km)
        editor.apply()
    }

    fun getMileage(): Float {
        return prefs.getFloat(KEY_CURRENT_MILEAGE, 0.0f)
    }
}