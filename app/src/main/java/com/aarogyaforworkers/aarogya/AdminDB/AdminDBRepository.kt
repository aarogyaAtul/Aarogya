package com.aarogyaforworkers.aarogya.AdminDB

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.storage.SettingPreferenceManager
import com.aarogyaforworkers.awsapi.APIManager
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import com.aarogyaforworkers.awsauth.S3Manager

class AdminDBRepository {

    private var testnav : NavHostController? = null

    fun setnav(navHostController: NavHostController){
        testnav = navHostController
    }

    fun getNav() : NavHostController?{
        return testnav
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: AdminDBRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AdminDBRepository().also { instance = it }
            }
    }

    private var isCreate = true
    private var lastVerificationOTP = ""
    private var profile = AdminProfile("","","","","","","","","","","","","")
    private var subUserProfile = SubUserProfile("","","","","","","","","","","","", "")
    private var subUserProfileToEdit = SubUserProfile("","","","","","","","","","","","", "")
    var subUserProfileToEditCopy = SubUserProfile("","","","","","","","","","","","", "")
    private var isAdminProfile : MutableState<AdminProfile> = mutableStateOf(profile)
    private var isGuestSessionsDeleted : MutableState<Boolean> = mutableStateOf(false)
    var guestSessionDeleted : State<Boolean> = isGuestSessionsDeleted
    private var isSubUserProfileList = mutableStateOf(mutableListOf(subUserProfile))
    private var isAdminProfileNotFound = mutableStateOf(false)
    private var isSubUserProfileCreatedUpdated = mutableStateOf(false)
    private var isSearchProfileNotFound = mutableStateOf(false)
    private var isUserRegistered = mutableStateOf(true)
    private var isAdminProfilePicUpdated = mutableStateOf("")
    var adminProfilePicUpdated : State<String> = isAdminProfilePicUpdated
    private var isUserNotRegistered = mutableStateOf(false)
    var adminProfileState : State<AdminProfile> = isAdminProfile
    var subUserSearchProfileListState : State<MutableList<SubUserProfile>> = isSubUserProfileList
    var adminProfileNotFoundState : State<Boolean> = isAdminProfileNotFound
    var subUserProfileNotFoundState : State<Boolean> = isSearchProfileNotFound
    var subUserProfileCreateUpdateState : State<Boolean> = isSubUserProfileCreatedUpdated
    var userRegisteredState : State<Boolean> = isUserRegistered
    var userNotRegisteredState : State<Boolean> = isUserNotRegistered
    var answer1= mutableStateOf("5")
    var answer2= mutableStateOf("5")
    var answer3= mutableStateOf("5")
    var answer4= mutableStateOf("5")
    var answer5= mutableStateOf("5")
    var answer6= mutableStateOf("5")
    var answer7= mutableStateOf("5")
    var subAnswer1= mutableStateOf("5")
    var subAnswer2= mutableStateOf("5")
    var subAnswer3= mutableStateOf("5")
    var subAnswer4= mutableStateOf("5")
    var subAnswer5= mutableStateOf("5")
    var subAnswer6= mutableStateOf("5")
    var subAnswer7= mutableStateOf("5")
    var subSubAnswer6= mutableStateOf("5")
    var subSubAnswer7= mutableStateOf("5")
    var subSubAnswer5= mutableStateOf("5")

    private var isNewSubUserProfile: MutableState<SubUserProfile?> = mutableStateOf(null)

    private var isCapturedSubUserImage: MutableState<ByteArray?> = mutableStateOf(null)

    // This function returns a concatenated string of all the answers given by the user in the form of "answer:subAnswer"
    fun getMedicalHistory() : String{
        // Create a variable for each answer-subAnswer pair
        val one = answer1.value+":"+subAnswer1.value
        val two = answer2.value+":"+subAnswer2.value
        val three = answer3.value+":"+subAnswer3.value
        val four = answer4.value+":"+subAnswer4.value
        val five = answer5.value+":"+subAnswer5.value
        val six = answer6.value+":"+subAnswer6.value
        val seven = answer7.value+":"+subAnswer7.value
        // Return the concatenated string of all the answer-subAnswer pairs
        return "$one,$two,$three,$four,$five,$six,$seven"
    }

    // This function initializes the APIManager for making API calls and passes an instance of the APICallbackResponse class as a parameter.
    fun initializeAPIManager(){
        APIManager.shared.initializeApiManager(APICallbackResponse())
    }

    // This function sets the value of the isCapturedSubUserImage LiveData to the given image ByteArray.
    fun setSubUserProfilePicture(image : ByteArray?){
        isCapturedSubUserImage.value = image
    }

    // This function updates the value of the isAdminProfilePicUpdated LiveData with the given String parameter.
    fun updateAdminProfilePicUpdateStatus(isUpdated:String){
        isAdminProfilePicUpdated.value = isUpdated
    }

    // Function to get user profile information based on search query
    fun getProfile(query : String){
        APIManager.shared.getProfile(query, true)
    }

    // Function to reset logged-in user information
    fun resetLoggedInUser() = APIManager.shared.resetLoggedInUser()

    // Function to get logged-in user information
    fun getLoggedInUser() = APIManager.shared.getLoggedInAdminProfile()

    // Retrieves a sub user profile from the API using their phone number
    fun getSubUserByPhone(phone : String){
        APIManager.shared.getSubUserByPhone(phone)
    }

    // Deletes a session with the specified session ID
    fun deleteSessionBySessionId(sessionId : String){
        APIManager.shared.deleteSessionById(sessionId)
    }

    // Returns the currently selected sub user profile
    fun getSelectedSubUserProfile() : SubUserProfile{
        return subUserProfileToEditCopy
    }

    // Returns the currently selected sub user profile for editing
    fun getSelectedUserProfileToEdit() : SubUserProfile{
        return subUserProfileToEdit
    }

    // Sets the new sub user profile for creation/editing
    fun setNewSubUserprofile(user: SubUserProfile){
        subUserProfile = user
        subUserProfileToEdit = user
    }

    fun setNewSubUserprofileCopy(user: SubUserProfile){
        subUserProfileToEditCopy = user
    }

//    fun isThereAnyUnSyncedSession(){
//
//    }

    /**
     * Updates the admin profile with the given [profile] data.
     * Resets the previous admin profile data.
     */
    fun updateAdminProfile(profile : AdminProfile){
        resetAdminProfile()
        isAdminProfile.value = profile
    }

    /**
     * Resets the admin profile data to default values.
     */
    private fun resetAdminProfile(){
        var profile = AdminProfile("","","","","","","","","","","","","")
        isAdminProfile.value = profile
    }

    /**
     * Updates the [isGuestSessionsDeleted] state to the given [isDeleted] value.
     */
    fun updateIsGuestSessionDeleted(isDeleted : Boolean){
        isGuestSessionsDeleted.value = isDeleted
    }

    /**
     * Updates the [isAdminProfileNotFound] state to the given [state] value.
     */
    fun updateAdminProfileNotFound(state : Boolean){
        isAdminProfileNotFound.value = state
    }

    /**
     * Updates the [isSearchProfileNotFound] state to the given [state] value.
     */
    fun updateSubUserProfileNotFound(state: Boolean){
        isSearchProfileNotFound.value = state
    }

    /**
     * Searches for sub-users matching the given query and updates the state with the search results.
     * @param query The search query.
     */
    fun searchUserByQuery(query : String){
        APIManager.shared.getProfile(query, false)
    }

    /**
     * Requests for a phone OTP to be sent to the given phone number.
     * @param phone The phone number to send the OTP to.
     */
    fun getPhoneOTP(phone: String){
        APIManager.shared.getEmailAndSendOTP(phone)
    }

    /**
     * Updates the state with the given list of sub-user profiles.
     * @param profileList The list of sub-user profiles.
     */
    fun updateSearchUserList(profileList : MutableList<SubUserProfile>){
        isSubUserProfileList.value = profileList
    }

    /**
     * Uploads the given admin profile picture to S3.
     * @param image The byte array representation of the image.
     */
    fun uploadAdminProfilePic(image : ByteArray){
        S3Manager.shared.uploadAdminProfilePic(image, MainActivity.authRepo.getAdminUID())
    }

    // Updates the admin profile picture in the API
    fun updateAdminProfilePic(profile: AdminProfile){
        APIManager.shared.updateAdminProfilePic(profile)
    }

    // Admin updates an existing sub-user's information in the API
    fun adminUpdateSubUser(user: SubUserProfile){
        isCreate = false
        // If a new profile picture was captured, upload it to S3 before updating the sub-user
        if(isCapturedSubUserImage.value != null){
            isNewSubUserProfile.value = user
            S3Manager.shared.uploadSubUserProfilePicFile(isCapturedSubUserImage.value!!, user.user_id)
        }else{
            APIManager.shared.updateSubUser(user)
        }
    }

    // Admin creates a new sub-user with a profile picture in the API
    private fun adminUpdateNewSubUserWithProfilePic(withProfileUrl : String){
        isCreate = false
        val user = isNewSubUserProfile.value
        user?.profile_pic_url = withProfileUrl
        if(user != null){
            APIManager.shared.updateSubUser(user)
        }
    }

    /**
     * This function creates a new sub user with the provided user details.
     * If a profile picture is captured for the user, it is uploaded to the server first and then the new user is created.
     * If no profile picture is captured, the user is created directly using APIManager.
     *
     * @param user the sub user profile details
     */
    fun adminCreateNewSubUser(user : SubUserProfile){
        isCreate = true
        if(isCapturedSubUserImage.value != null){
            isNewSubUserProfile.value = user
            S3Manager.shared.uploadSubUserProfilePicFile(isCapturedSubUserImage.value!!, user.user_id)
        }else{
            APIManager.shared.createNewSubUser(user)
        }
    }

    /**
     * This function deletes all sessions for a guest with the provided id.
     *
     * @param id the id of the guest
     */
    fun deleteAllSessionForGuest(id : String){
        APIManager.shared.deleteGuestAllSession(id)
        Log.d("TAG", "deleteAllSessionForGuest: GuestID $MainActivity.adminDBRepo.getLoggedInUser().admin_id")
    }

    /**
     * This function creates a new sub user with the provided user details and profile picture url.
     *
     * @param withProfileUrl the profile picture url for the user
     */
    private fun adminCreateNewSubUserWithProfilePic(withProfileUrl : String){
        isCreate = true
        val user = isNewSubUserProfile.value
        user?.profile_pic_url = withProfileUrl
        if(user != null){
            APIManager.shared.createNewSubUser(user)
        }
    }

    // Function to create or update a sub-user profile with a profile picture
    fun createOrUpdateSubUserWithProfilePic(withProfileUrl : String){
        // Depending on whether we're creating a new sub-user or updating an existing one, call the appropriate API method
        when(isCreate){
            true -> adminCreateNewSubUserWithProfilePic(withProfileUrl)
            false -> adminUpdateNewSubUserWithProfilePic(withProfileUrl)
        }
    }

    // Function to update a sub-user's phone number in the database
    private fun adminUpdateSubUserPhone(user : SubUserProfile){
        APIManager.shared.updateSubUser(user)
    }

    // Function to send a verification code to a sub-user's phone number
    fun sendSubUserVerificationCode(phone: String){
        APIManager.shared.sendVerificationOTP(phone)
    }

    // Function to update the state of whether a user is registered or not
    fun updateUserRegisteredState(state: Boolean){
        isUserRegistered.value = false
    }

    // Set the flag indicating that the user is not registered
    fun updateUserNotRegisteredState(state: Boolean){
        isUserNotRegistered.value = true
    }

    // Reset all the state flags
    fun resetStates(){
        isAdminProfileNotFound.value = false
        isSubUserProfileCreatedUpdated.value = false
        isSearchProfileNotFound.value = false
        isUserRegistered.value = true
        isUserNotRegistered.value = false
    }

    // Reset all the medical answers to the default value
    fun resetMedicalAnswers(){
        answer1.value = "5"
        answer2.value = "5"
        answer3.value = "5"
        answer4.value = "5"
        answer5.value = "5"
        answer6.value = "5"
        answer7.value = "5"
        subAnswer1.value = "5"
        subAnswer2.value = "5"
        subAnswer3.value = "5"
        subAnswer4.value = "5"
        subAnswer5.value = "5"
        subAnswer6.value = "5"
        subAnswer7.value = "5"
        subSubAnswer5.value = "5"
        subSubAnswer6.value = "5"
        subSubAnswer7.value = "5"
    }

    /**
     * This function sets the last verification OTP.
     * @param otp: the OTP to be set.
     */
    fun setLastVerificationOTP(otp : String){
        lastVerificationOTP = otp
    }

    /**
     * This function checks if the user-entered OTP is equal to the last verification OTP.
     * @param userOTP: the OTP entered by the user.
     * @return: returns true if the user-entered OTP is equal to the last verification OTP, false otherwise.
     */
    fun checkVerificationCode(userOTP : String) : Boolean{
        return lastVerificationOTP == userOTP
    }

    /**
     * This function updates the phone verification status of the sub-user.
     * @param user: the sub-user whose phone verification status needs to be updated.
     */
    fun updateSubUserPhoneVerificationStatus(user: SubUserProfile){
        adminUpdateSubUserPhone(user)
    }

    /**
     * This function updates the state of sub-user profile creation/update.
     * @param isSuccess: true if the sub-user profile creation/update is successful, false otherwise.
     */
    fun updateSubUserProfileCreateUpdateState(isSuccess: Boolean){
        isSubUserProfileCreatedUpdated.value = isSuccess
    }


    // Admin settings options
    private var isHeightUnit = mutableStateOf(0)          // 0 - cm, 1 - inch
    private var isWightUnit = mutableStateOf(0)           // 0 - kg, 1 - lbs
    private var isTempUnit = mutableStateOf(0)            // 0 - °C, 1 - °F
    var heightUnit : State<Int> = isHeightUnit
    var weightUnit : State<Int> = isWightUnit
    var tempUnit : State<Int> = isTempUnit

    /**
     * This function updates the current units for height, weight, and temperature based on the user's
     * previously saved preferences. It retrieves the saved unit preferences from the shared preferences
     * using the SettingPreferenceManager singleton instance and sets the corresponding LiveData
     * objects to the retrieved values.
     *
     * @param context The context of the calling activity or fragment.
     */
    fun updateLastSavedUnits(context: Context){
        // Retrieve the saved height unit preference from the shared preferences and set the
        // corresponding LiveData object to the retrieved value.
        isHeightUnit.value = SettingPreferenceManager.getInstance(context).getHeightKey()

        // Retrieve the saved weight unit preference from the shared preferences and set the
        // corresponding LiveData object to the retrieved value.
        isWightUnit.value = SettingPreferenceManager.getInstance(context).getWeightKey()

        // Retrieve the saved temperature unit preference from the shared preferences and set the
        // corresponding LiveData object to the retrieved value.
        isTempUnit.value = SettingPreferenceManager.getInstance(context).getTempKey()
    }

    /**
     * Updates the height unit setting in the app and saves it to the shared preferences.
     * @param unit an integer value representing the selected unit (0 for centimeters, 1 for inches)
     * @param context the application context
     */
    fun updateHeightUnit(unit : Int, context: Context){
        // 0 - cm, 1 - inch
        isHeightUnit.value = unit
        SettingPreferenceManager.getInstance(context).saveHeightUnit(unit)
    }

    /**
     * Updates the weight unit setting in the app and saves it to the shared preferences.
     * @param unit an integer value representing the selected unit (0 for kilograms, 1 for pounds)
     * @param context the application context
     */
    fun updateWeightUnit(unit : Int, context: Context){
        // 0 - kg, 1 - lbs
        isWightUnit.value = unit
        SettingPreferenceManager.getInstance(context).saveWeightUnit(unit)
    }

    /**
     * Updates the temperature unit setting in the app and saves it to the shared preferences.
     * @param unit an integer value representing the selected unit (0 for Celsius, 1 for Fahrenheit)
     * @param context the application context
     */
    fun updateTempUnit(unit : Int, context: Context){
        // 0 - °C, 1 - °F
        isTempUnit.value = unit
        SettingPreferenceManager.getInstance(context).saveTempUnit(unit)
    }

    fun getTempUnit() : String{
        return if(isTempUnit.value == 0) "°F" else "°C"
    }



    fun getWeightUnit() : String{
        return if(isWightUnit.value == 0) "kg" else "lbs"
    }

    fun getHeightUnit() : String{ return if( isHeightUnit.value == 0) "cm" else "ft, in"   }

    /**
     * Converts a temperature value from Celsius to Fahrenheit or vice versa based on the selected unit.
     *
     * @param tempInC The temperature value in Celsius.
     * @return A string representation of the temperature value in the selected unit.
     */
    fun getTempBasedOnUnitSet(tempInC : Double?) : String{
        // Determine the selected temperature unit from the shared preferences.
        return when(isTempUnit.value){
            // If Celsius is selected, return the temperature value followed by "°C".
            1 -> {
                if(tempInC == null) return "" else "$tempInC °C"
            }
            // If Fahrenheit is selected, convert the temperature value to Fahrenheit and return it followed by "°F".
            0 -> {
                if(tempInC == null) return ""
                val tempF = tempInC?.times(1.8)?.plus(32)
                tempF.let { "%.1f °F".format(it) } ?: ""
            }
            // If the selected unit is neither Celsius nor Fahrenheit, return the temperature value followed by "°C".
            else -> {
                if(tempInC == null) return "" else "$tempInC °C"
            }
        }
    }


    fun getTempBasedOnUnit(tempInC : Double?) : String{
        // Determine the selected temperature unit from the shared preferences.
        return when(isTempUnit.value){
            // If Celsius is selected, return the temperature value followed by "°C".
            1 -> {
                if(tempInC == null) return "" else "$tempInC"
            }
            // If Fahrenheit is selected, convert the temperature value to Fahrenheit and return it followed by "°F".
            0 -> {
                if(tempInC == null) return ""
                val tempF = tempInC?.times(1.8)?.plus(32)
                tempF.let { "%.1f".format(it) } ?: ""
            }
            // If the selected unit is neither Celsius nor Fahrenheit, return the temperature value followed by "°C".
            else -> {
                if(tempInC == null) return "" else "$tempInC"
            }
        }
    }





    /**
    Returns the height based on the user's selected unit set.
    @param heightInCm The height in centimeters.
    @return The formatted height string based on the user's selected unit set.
     */
    fun getHeightBasedOnUnitSet(heightInCm : Double?) : String {

        return when(isHeightUnit.value){
            // If the user's selected unit set is metric, return the height in centimeters.
            0 -> {
                if(heightInCm == null) return "" else "${heightInCm.toInt()} cm"
            }
            // If the user's selected unit set is imperial, convert the height to feet and inches and return as a string.
            1 -> {
                if(heightInCm != null){
                   val inches = heightInCm / 2.54
                   val wholeInches = inches.toInt()
                   val remainingInches = inches - wholeInches
                   // Step 4: Convert to feet
                   val feet = wholeInches / 12
                   // Step 5: Get remaining inches after converting to feet
                   val inchesAfterFeet = wholeInches % 12
                   return "$feet ft ${String.format("%.0f", inchesAfterFeet + remainingInches)} in"
                }else{
                    return ""
                }
            }

            // If the user's selected unit set is invalid, return the height in centimeters.
            else -> {
                   if(heightInCm == null) return "" else "${heightInCm.toInt()} cm"
            }
        }
    }

    /**
    Returns the height based on the user's selected unit set.
    @param heightInCm The height in centimeters.
    @return The formatted height string based on the user's selected unit set.
     */
    fun getHeightBasedOnUnitSetWithoutUnit(heightInCm : Double?) : String {

        return when(isHeightUnit.value){
            // If the user's selected unit set is metric, return the height in centimeters.
            0 -> {
                if(heightInCm == null) return "" else "${heightInCm.toInt()}"
            }
            // If the user's selected unit set is imperial, convert the height to feet and inches and return as a string.
            1 -> {
                if(heightInCm != null){
                    val inches = heightInCm / 2.54
                    val wholeInches = inches.toInt()
                    val remainingInches = inches - wholeInches
                    // Step 4: Convert to feet
                    val feet = wholeInches / 12
                    // Step 5: Get remaining inches after converting to feet
                    val inchesAfterFeet = wholeInches % 12
                    return "$feet, ${String.format("%.0f", inchesAfterFeet + remainingInches)}"
                }else{
                    return ""
                }
            }

            // If the user's selected unit set is invalid, return the height in centimeters.
            else -> {
                if(heightInCm == null) return "" else "${heightInCm.toInt()}"
            }
        }
    }

    /**
     * Returns the weight in the unit selected by the user based on the weight provided in kilograms.
     *
     * @param weightInKg The weight in kilograms.
     * @return The weight in the unit selected by the user.
     */
    fun getWeightBasedOnUnitSet(weightInKg : Double?) : String{

        return when(isWightUnit.value){
            // Case when weight unit is set to kg
            0 -> {
                // If weightInKg is null, return empty string. Otherwise, return the weight in kg with the unit.
                if(weightInKg == null) return "" else "$weightInKg kg"
            }

            // Case when weight unit is set to lbs
            1 -> {
                // If weightInKg is null, return empty string. Otherwise, convert the weight to lbs and return it with the unit.
                if(weightInKg == null) return ""
                val weightLbs = weightInKg.times(2.20462)
                return weightLbs.let { "%.2f lbs".format(it) } ?: ""
            }

            // Default case if weight unit is not set or an invalid value is selected
            else -> {
                // If weightInKg is null, return empty string. Otherwise, return the weight in kg with the unit.
                if(weightInKg == null) return "" else "$weightInKg kg"
            }
        }
    }


    fun getWeightBasedOnUnits(weightInKg : Double?) : Double{

        return when(isWightUnit.value){
            // Case when weight unit is set to kg
            0 -> {
                // If weightInKg is null, return empty string. Otherwise, return the weight in kg with the unit.
                if(weightInKg == null) return 0.0 else weightInKg
            }

            // Case when weight unit is set to lbs
            1 -> {
                // If weightInKg is null, return empty string. Otherwise, convert the weight to lbs and return it with the unit.
                if (weightInKg == null) return 0.0
                val weightLbs = weightInKg * 2.20462
                return "%.2f".format(weightLbs).toDouble()
            }

            // Default case if weight unit is not set or an invalid value is selected
            else -> {
                // If weightInKg is null, return empty string. Otherwise, return the weight in kg with the unit.
                if(weightInKg == null) return 0.0 else weightInKg
            }
        }
    }




    

}