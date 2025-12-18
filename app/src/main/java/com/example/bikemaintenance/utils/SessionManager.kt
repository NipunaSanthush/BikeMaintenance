package com.example.bikemaintenance.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_NAME = "name"
        const val KEY_BIKE_MODEL = "bikeModel"
        const val KEY_LICENSE_PLATE = "licensePlate"
        const val KEY_PROFILE_IMAGE = "profile_image"
        const val KEY_CURRENT_MILEAGE = "current_mileage"
        const val KEY_IS_TRACKING = "is_tracking"

        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
        const val KEY_MOBILE = "mobile"
        const val KEY_ADDRESS = "address"
        const val KEY_BIRTHDAY = "birthday"
        const val KEY_BIKE_BRAND = "bike_brand"
    }

    fun createAccount(
        name: String, username: String, pass: String,
        mobile: String, address: String, birthday: String,
        bikeBrand: String, bikeModel: String, licensePlate: String
    ) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, pass)
        editor.putString(KEY_MOBILE, mobile)
        editor.putString(KEY_ADDRESS, address)
        editor.putString(KEY_BIRTHDAY, birthday)
        editor.putString(KEY_BIKE_BRAND, bikeBrand)
        editor.putString(KEY_BIKE_MODEL, bikeModel)
        editor.putString(KEY_LICENSE_PLATE, licensePlate)
        editor.apply()
    }

    fun validateLogin(usernameInput: String, passwordInput: String): Boolean {
        val savedUsername = prefs.getString(KEY_USERNAME, null)
        val savedPassword = prefs.getString(KEY_PASSWORD, null)

        return if (savedUsername == usernameInput && savedPassword == passwordInput) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true)
            editor.apply()
            true
        } else {
            false
        }
    }

    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user[KEY_NAME] = prefs.getString(KEY_NAME, "User")!!
        user[KEY_BIKE_MODEL] = prefs.getString(KEY_BIKE_MODEL, "Bike")!!
        user[KEY_LICENSE_PLATE] = prefs.getString(KEY_LICENSE_PLATE, "")!!

        user[KEY_USERNAME] = prefs.getString(KEY_USERNAME, "")!!
        user[KEY_MOBILE] = prefs.getString(KEY_MOBILE, "")!!
        user[KEY_ADDRESS] = prefs.getString(KEY_ADDRESS, "")!!
        user[KEY_BIRTHDAY] = prefs.getString(KEY_BIRTHDAY, "")!!
        user[KEY_BIKE_BRAND] = prefs.getString(KEY_BIKE_BRAND, "")!!

        return user
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
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

    fun saveMileage(km: Float) {
        editor.putFloat(KEY_CURRENT_MILEAGE, km)
        editor.apply()
    }

    fun getMileage(): Float {
        return prefs.getFloat(KEY_CURRENT_MILEAGE, 0.0f)
    }

    fun setTrackingState(isTracking: Boolean) {
        editor.putBoolean(KEY_IS_TRACKING, isTracking)
        editor.apply()
    }

    fun isTracking(): Boolean {
        return prefs.getBoolean(KEY_IS_TRACKING, false)
    }
}