package com.aarogyaforworkers.aarogya.storage

import android.content.Context

class SettingPreferenceManager private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("guestSession", Context.MODE_PRIVATE)

    private val tempKey = "temp"
    private val heightKey = "height"
    private val weightKey = "weight"

    /**
     * Saves the selected temperature unit to the app's shared preferences.
     *
     * @param value The selected temperature unit.
     */
    fun saveTempUnit(value : Int){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected temperature unit using the putInt() method
        // Apply the changes using the apply() method to save the selected temperature unit to the app's shared preferences
        sharedPreferences.edit().putInt(tempKey, value).apply()
    }

    /**
     * Saves the selected height unit to the app's shared preferences.
     *
     * @param value The selected height unit.
     */
    fun saveHeightUnit(value : Int){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected height unit using the putInt() method
        // Apply the changes using the apply() method to save the selected height unit to the app's shared preferences
        sharedPreferences.edit().putInt(heightKey, value).apply()
    }

    /**
     * Saves the selected weight unit to the app's shared preferences.
     *
     * @param value The selected weight unit.
     */
    fun saveWeightUnit(value : Int){
        // Use the edit() method of the shared preferences object to create a new editor and store the selected weight unit using the putInt() method
        // Apply the changes using the apply() method to save the selected weight unit to the app's shared preferences
        sharedPreferences.edit().putInt(weightKey, value).apply()
    }

    /**
     * Retrieves the selected temperature unit from the app's shared preferences.
     *
     * @return The selected temperature unit, or 0 if no unit has been selected.
     */
    fun getTempKey() : Int{
        // Use the getInt() method of the shared preferences object to retrieve the selected temperature unit, or 0 if no unit has been selected
        return sharedPreferences.getInt(tempKey, 0)
    }

    /**
     * Retrieves the selected height unit from the app's shared preferences.
     *
     * @return The selected height unit, or 0 if no unit has been selected.
     */
    fun getHeightKey() : Int{
        // Use the getInt() method of the shared preferences object to retrieve the selected height unit, or 0 if no unit has been selected
        return sharedPreferences.getInt(heightKey, 0)
    }

    /**
     * Retrieves the selected weight unit from the app's shared preferences.
     *
     * @return The selected weight unit, or 0 if no unit has been selected.
     */
    fun getWeightKey() : Int{
        // Use the getInt() method of the shared preferences object to retrieve the selected weight unit, or 0 if no unit has been selected
        return sharedPreferences.getInt(weightKey, 0)
    }

    companion object {

        @Volatile
        private var instance: SettingPreferenceManager? = null

        // Get instance of SettingPreferenceManager to access the functions
        fun getInstance(context: Context): SettingPreferenceManager =
            instance ?: synchronized(this) {
                instance ?: SettingPreferenceManager(context).also { instance = it }
            }
    }

}