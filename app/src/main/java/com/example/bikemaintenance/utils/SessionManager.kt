package com.example.bikemaintenance.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(private val context: Context) {

    private val globalPrefs: SharedPreferences = context.getSharedPreferences("global_app_prefs", Context.MODE_PRIVATE)
    private val globalEditor: SharedPreferences.Editor = globalPrefs.edit()

    companion object {
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_CURRENT_USERNAME = "current_username"

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

    private fun getUserPrefs(username: String): SharedPreferences {
        return context.getSharedPreferences("user_data_$username", Context.MODE_PRIVATE)
    }

    private fun getCurrentUserPrefs(): SharedPreferences? {
        val currentUsername = globalPrefs.getString(KEY_CURRENT_USERNAME, null)
        return if (currentUsername != null) {
            getUserPrefs(currentUsername)
        } else {
            null
        }
    }


    fun createAccount(
        name: String, username: String, pass: String,
        mobile: String, address: String, birthday: String,
        bikeBrand: String, bikeModel: String, licensePlate: String
    ) {
        val userPrefs = getUserPrefs(username)
        val editor = userPrefs.edit()

        editor.putString(KEY_NAME, name)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, pass)
        editor.putString(KEY_MOBILE, mobile)
        editor.putString(KEY_ADDRESS, address)
        editor.putString(KEY_BIRTHDAY, birthday)
        editor.putString(KEY_BIKE_BRAND, bikeBrand)
        editor.putString(KEY_BIKE_MODEL, bikeModel)
        editor.putString(KEY_LICENSE_PLATE, licensePlate)

        editor.putFloat(KEY_CURRENT_MILEAGE, 0f)
        editor.apply()

        createLoginSession(username)
    }

    fun validateLogin(usernameInput: String, passwordInput: String): Boolean {
        val userPrefs = getUserPrefs(usernameInput)
        val savedPassword = userPrefs.getString(KEY_PASSWORD, null)

        return if (savedPassword != null && savedPassword == passwordInput) {
            createLoginSession(usernameInput)
            true
        } else {
            false
        }
    }

    private fun createLoginSession(username: String) {
        globalEditor.putBoolean(KEY_IS_LOGGED_IN, true)
        globalEditor.putString(KEY_CURRENT_USERNAME, username)
        globalEditor.apply()
    }

    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        val prefs = getCurrentUserPrefs()

        if (prefs != null) {
            user[KEY_NAME] = prefs.getString(KEY_NAME, "User")!!
            user[KEY_BIKE_MODEL] = prefs.getString(KEY_BIKE_MODEL, "Bike")!!
            user[KEY_LICENSE_PLATE] = prefs.getString(KEY_LICENSE_PLATE, "")!!
            user[KEY_USERNAME] = prefs.getString(KEY_USERNAME, "")!!
            user[KEY_MOBILE] = prefs.getString(KEY_MOBILE, "")!!
            user[KEY_ADDRESS] = prefs.getString(KEY_ADDRESS, "")!!
            user[KEY_BIRTHDAY] = prefs.getString(KEY_BIRTHDAY, "")!!
            user[KEY_BIKE_BRAND] = prefs.getString(KEY_BIKE_BRAND, "")!!
        }
        return user
    }

    fun isLoggedIn(): Boolean {
        return globalPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logoutUser() {
        globalEditor.clear()
        globalEditor.commit()
    }

    fun saveProfileImage(uri: String) {
        getCurrentUserPrefs()?.edit()?.putString(KEY_PROFILE_IMAGE, uri)?.apply()
    }

    fun getProfileImage(): String? {
        return getCurrentUserPrefs()?.getString(KEY_PROFILE_IMAGE, null)
    }

    fun removeProfileImage() {
        getCurrentUserPrefs()?.edit()?.remove(KEY_PROFILE_IMAGE)?.apply()
    }

    fun saveMileage(km: Float) {
        getCurrentUserPrefs()?.edit()?.putFloat(KEY_CURRENT_MILEAGE, km)?.apply()
    }

    fun getMileage(): Float {
        return getCurrentUserPrefs()?.getFloat(KEY_CURRENT_MILEAGE, 0.0f) ?: 0.0f
    }

    fun setTrackingState(isTracking: Boolean) {
        getCurrentUserPrefs()?.edit()?.putBoolean(KEY_IS_TRACKING, isTracking)?.apply()
    }

    fun isTracking(): Boolean {
        return getCurrentUserPrefs()?.getBoolean(KEY_IS_TRACKING, false) ?: false
    }
}