package com.aarogyaforworkers.aarogya.SubUser

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.SubUserMedicalHistory
import com.aarogyaforworkers.awsapi.APIManager
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile

var isItFromHistoryPage = false

class SubUserDBRepository {

    private var session = Session("","","","","","","","","","","","","","","","","")

    private var defSessionState = SessionStates(false, false, false, false, false)

    private var isCurrentUserSessionState = mutableStateOf(defSessionState)

    private var isCurrentPhoneAllReadyRegistered : MutableState<Boolean?> = mutableStateOf(null)

    var isCurrentSessionSaved = false

    var isCurrentSessionUpdated = false

    var isForSaveAndRestartAction = false

    var currentPhoneAllReadyRegistered : State<Boolean?> = isCurrentPhoneAllReadyRegistered

    var currentSessionState : State<SessionStates> = isCurrentUserSessionState

    private var sessionUpdateState = SessionStates(false, false, false, true, false)

    private var sessionSaveState = SessionStates(false, false, true, false,false)

    private var sessionRestartState = SessionStates(true, false, false, false,false)

    private var sessionResetState = SessionStates(false, true, false, false,false)

    private var sessionFailedToSaveOrRestartState = SessionStates(false, false, false, false,true)

    var selectedPhoneNoForVerification = mutableStateOf("")

    fun updateCurrentPhoneRegistrationState(state : Boolean?){
        isCurrentPhoneAllReadyRegistered.value = state
    }

    /* 1 - when a admin search and selects the user update or reset the states
    *  2 - when a admin restarts the session from the data collection page
    * **/
    fun updateSessionState(states: SessionStates){
        isCurrentUserSessionState.value = states
    }

    fun updateSessionSaveState(){
        updateSessionState(sessionSaveState)
    }

    fun updateSessionRestartState(){
        updateSessionState(sessionRestartState)
    }

    fun restartSession(){
        when(bufferThere.value){
            true -> {
                isForSaveAndRestartAction = true
                if(isCurrentSessionSaved) updateSession() else saveSession()
            }

            false -> {
                updateSessionRestartState()
            }
        }
    }

    fun resetSession(){
        when(isCurrentSessionSaved){
            true -> {
                MainActivity.adminDBRepo.deleteSessionBySessionId(getSessionId())
            }

            false -> {
                updateSessionResetState()
            }
        }
    }

    fun updateSessionResetState(){
        updateSessionState(sessionResetState)
    }

    fun updateSessionUpdateState(){
        updateSessionState(sessionUpdateState)
    }

    fun updateDefaultSessionState(){
        updateSessionState(defSessionState)
    }

    fun updateSessionFailedToSave(){
        updateSessionState(sessionFailedToSaveOrRestartState)
    }

    fun saveOrUpdateSession(){
        isForSaveAndRestartAction = false
        if(isCurrentSessionSaved) updateSession() else saveSession()
    }

    fun resetStates(){
        isCurrentSessionSaved = false
        isCurrentSessionUpdated = false
        isForSaveAndRestartAction = false
    }

    private fun saveSession(){
        if(MainActivity.pc300Repo.isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null) {
            MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
        }else{
            MainActivity.subUserRepo.createAndUploadSession(MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo)
        }
    }

    private fun updateSession(){
        if(MainActivity.pc300Repo.isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null) {
            MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
        }else{
            MainActivity.subUserRepo.createAndUploadSession(MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo)
        }
    }

    fun checkAndSaveOrUpdateSession(){
        /* when session is saved then save state will change to true if it's not saved it will remain false  **/
        when{
            !isCurrentUserSessionState.value.isSave -> {
                saveSession()
            }

            isCurrentUserSessionState.value.isSave && isCurrentUserSessionState.value.isUpdate -> {
                updateSession()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startFetchingAfterUserCreation(){
        createNewSession()
        val handler = Handler()
        handler.postDelayed({
            if(MainActivity.omronRepo.isDeviceConnected) {
                if(!MainActivity.omronRepo.isReadyForFetch){
                    MainActivity.omronRepo.setUserProfile()
                    MainActivity.omronRepo.updateDeviceStatus("Syncing")
                }
            }else{
                MainActivity.omronRepo.updateDeviceStatus("")
            }
            getSessionsByUserID(MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
        }, 4000)
    }

    fun startFetchingFromOmronDevice(){
        val handler = Handler()
        handler.postDelayed({
            if(MainActivity.omronRepo.isDeviceConnected) {
                if(!MainActivity.omronRepo.isReadyForFetch){
                    MainActivity.omronRepo.setUserProfile()
                    MainActivity.omronRepo.updateDeviceStatus("Syncing")
                }
            }else{
                MainActivity.omronRepo.updateDeviceStatus("")
            }
            getSessionsByUserID(MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
        }, 4000)
    }

    var isThereAnyChange = mutableStateOf(false)

    private var subUserMedicalHistoryAnswer = SubUserMedicalHistory(0,0,0,"")

    private var isSubUserMedicalHistoryAnswersList = mutableStateOf(mutableListOf(subUserMedicalHistoryAnswer))

    private var newSessionPerformed = mutableStateOf(session)

    private var isSubUserSessionsList = mutableStateOf(mutableListOf(session))

    private var isSessionFetchFailed = mutableStateOf(false)

    private var isSessionUpdated = mutableStateOf(false)

    private var isUploadingSession = mutableStateOf(false)

    var isResetQuestion = mutableStateOf(false)

    var uploadingSession : State<Boolean> = isUploadingSession

    var isSelected1A = mutableStateOf(false)

    var isSelected1B = mutableStateOf(false)

    var isSelected1C =  mutableStateOf(false)

    var isSelected2A = mutableStateOf(false)

    var isSelected2B = mutableStateOf(false)

    var isSelected3A = mutableStateOf(false)

    var isSelected3B = mutableStateOf(false)

    var showProgress = mutableStateOf(false)

    fun updateIsUploading(uploadingSession : Boolean){
        isUploadingSession.value = uploadingSession
    }

    fun updateProgressState(show : Boolean){
        showProgress.value = show
    }

    private val isBufferThere = mutableStateOf(false)

    var bufferThere : State<Boolean> = isBufferThere

    fun updateIsBufferThere(isThere : Boolean){
        isBufferThere.value = isThere
    }

    var lastSavedSession : Session? = null

    var lastAvgSession : Session = Session("","","","","","","","","","","","","","","","","")

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewSession(){
        resetPerformedSession()
        MainActivity.pc300Repo.updateDateTime()
        MainActivity.subUserRepo.createSessionId(MainActivity.pc300Repo, MainActivity.adminDBRepo)
    }

    val sessionUpdated : State<Boolean> = isSessionUpdated

    val changeInProfile : State<Boolean> = isThereAnyChange

    val subUserMedicalHistory : State<MutableList<SubUserMedicalHistory>> = isSubUserMedicalHistoryAnswersList

    var selectedUserId = ""

    private var sessionId = mutableStateOf("")

    var isUserAllReadyPresent : MutableState<Boolean?> = mutableStateOf(null)

    val  sessions : State<MutableList<Session>>  = isSubUserSessionsList

    fun getSession() : Session{
        val subUser = MainActivity.adminDBRepo.getSelectedSubUserProfile()
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        val adminId = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        val dob = subUser.dob.replace("/", "")
        val subUserId = subUser.user_id
        val sys = MainActivity.pc300Repo.sys.value
        val dia = MainActivity.pc300Repo.dia.value
        val hr = MainActivity.pc300Repo.heartRate.value
        val spo2 = MainActivity.pc300Repo.spO2.value
        val temp = MainActivity.pc300Repo.temp.value
        val glu = MainActivity.pc300Repo.glu.value
        val location = MainActivity.locationRepo.userLocation.value
        var userLocation = ""
        if(location != null){
            userLocation = location.address+"/"+location.city+"/"+location.postalCode+"/"+location.country+"/"+location.lat+"/"+location.lon
        }
        val sessionUUID = getSessionId()

        val pc303Device = MainActivity.pc300Repo.deviceId.takeLast(4)
        val omronDevice = MainActivity.omronRepo.deviceId.takeLast(4)
        val deviceId = pc303Device+omronDevice

        val sessionDate = MainActivity.pc300Repo.getSessionDate()
        val sessionTime = MainActivity.pc300Repo.getSessionTime()
        val ecgFile = ""
        val weightInfo = MainActivity.omronRepo.latestUserWeightInfo.value
        var bmi = ""
        if(weightInfo?.bmi != null){
            bmi = weightInfo.bmi.toString()
        }
        var bodyfat = ""
        if(weightInfo?.bodyFat != null){
            bodyfat = weightInfo.bodyFat.toString()
        }
        val answers = MainActivity.pc300Repo.answer1.value+MainActivity.pc300Repo.answer2.value+MainActivity.pc300Repo.answer3.value
//        return Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2, bmi, bodyfat,temp, ecgFile,answers,"t",userLocation)

        //For now replace BMI with glu
        return Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2, glu, bodyfat,temp, ecgFile,answers,"t",userLocation)
    }

    /**
     * Creates a new session object with information on the current session, including vital signs, location, and answers to questions,
     * and sends it to the cloud.
     *
     * @param withEcgFileLink The link to the ECG file associated with the current session.
     * @param adminDBRepository The repository for admin user data.
     * @param pC300Repository The repository for PC300 data.
     * @param locationRepository The repository for location data.
     */
    fun updateSessionInCloud(withEcgFileLink : String, adminDBRepository: AdminDBRepository, pC300Repository: PC300Repository, locationRepository: LocationRepository){
        updateIsUploading(true)
        val subUser = adminDBRepository.getSelectedSubUserProfile()
        val admin = adminDBRepository.getLoggedInUser()
        val adminId = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        val dob = subUser.dob.replace("/", "")
        val MM = dob.take(2)
        val YY = dob.takeLast(2)
        val subUserId = subUser.user_id
        val sys = pC300Repository.sys.value
        val dia = pC300Repository.dia.value
        val hr = pC300Repository.heartRate.value
        val spo2 = pC300Repository.spO2.value
        val temp = pC300Repository.temp.value
        val glu = pC300Repository.glu.value

        val location = locationRepository.userLocation.value
        var userLocation = ""
        if(location != null){
            userLocation = location.address+"/"+location.city+"/"+location.postalCode+"/"+location.country+"/"+location.lat+"/"+location.lon
        }
        val sessionUUID = getSessionId()
        val pc303Device = MainActivity.pc300Repo.deviceId.takeLast(4)
        val omronDevice = MainActivity.omronRepo.deviceId.takeLast(4)
        val deviceId = pc303Device+omronDevice
        val sessionDate = pC300Repository.getSessionDate()
        val sessionTime = pC300Repository.getSessionTime()
        val ecgFile = withEcgFileLink
        val weightInfo = MainActivity.omronRepo.latestUserWeightInfo.value
        var bmi = ""
        if(weightInfo?.bmi != null){
            bmi = weightInfo.bmi.toString()
        }
        var bodyfat = ""
        if(weightInfo?.bodyFat != null){
            bodyfat = weightInfo.bodyFat.toString()
        }
        val answers = pC300Repository.answer1.value+pC300Repository.answer2.value+pC300Repository.answer3.value

        // for now replace bmi with glucose
//        newSessionPerformed.value = Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2, bmi, bodyfat,temp, ecgFile,answers,"t",userLocation)

        newSessionPerformed.value = Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2, glu, bodyfat,temp, ecgFile,answers,"t",userLocation)


        lastSavedSession = newSessionPerformed.value

        when(isCurrentSessionSaved){
            true -> {
                updateFullSession(newSessionPerformed.value)
            }
            false -> {
                createNewSession(newSessionPerformed.value)
            }
        }
    }

    /**
     * Resets the current session object.
     */
    fun resetPerformedSession(){
        session = Session("","","","","","","","","","","","","","","","","")
        newSessionPerformed.value = session
    }

    /**
     * Creates a unique session ID based on the device ID, admin ID, and session time.
     *
     * @param pC300Repository The repository for PC300 data.
     * @param adminDBRepository The repository for admin user data.
     */
    fun createSessionId(pC300Repository: PC300Repository, adminDBRepository: AdminDBRepository){
//        if(adminDBRepository.getSelectedSubUserProfile().phone.length == 10) adminDBRepository.getSelectedSubUserProfile().phone = "91"+adminDBRepository.getSelectedSubUserProfile().phone
        if(pC300Repository.deviceId.isEmpty()) pC300Repository.deviceId = "XXXXXXXX"
        sessionId.value = pC300Repository.getSessionTime().replace(":", "")+":"+pC300Repository.deviceId.takeLast(4).replace(":", "")+":"+adminDBRepository.getLoggedInUser().admin_id.takeLast(6)
    }
    /**
     * This function updates the value of the isThereAnyChange LiveData variable in the SubUserRepo
     * based on whether there is a change or not.
     *
     * @param isChanged Boolean value representing whether there is a change or not
     */
    fun updateChange(isChanged : Boolean){
        // Update the value of the isThereAnyChange variable in the SubUserRepo
        MainActivity.subUserRepo.isThereAnyChange.value = isChanged
    }

    /**
     * This function returns the value of the sessionId LiveData variable.
     *
     * @return String value representing the sessionId
     */
    fun getSessionId() = sessionId.value

    /**
     * This function creates and uploads a new session to the cloud using the data provided
     * in the function parameters and LiveData variables.
     *
     * @param adminDBRepository An instance of the AdminDBRepository class
     * @param pC300Repository An instance of the PC300Repository class
     * @param locationRepository An instance of the LocationRepository class
     */
    fun createAndUploadSession(adminDBRepository: AdminDBRepository, pC300Repository: PC300Repository, locationRepository: LocationRepository){

        updateIsUploading(true)

        // Get the selected sub user profile and logged in user from the AdminDBRepository
        val subUser = adminDBRepository.getSelectedSubUserProfile()
        val admin = adminDBRepository.getLoggedInUser()

        // Create the adminId using the first four characters of the admin's first name and the last four digits of their phone number
        val adminId = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')

        // Get the date of birth of the selected sub user and extract the month and year
        val dob = subUser.dob.replace("/", "")
        val MM = dob.take(2)
        val YY = dob.takeLast(2)

        // Get the subUserId from the selected sub user profile
        val subUserId = subUser.user_id

        // Get the latest weight info from the OmronRepo
        val weightInfo = MainActivity.omronRepo.latestUserWeightInfo.value

        // Get the latest values for systolic blood pressure, diastolic blood pressure, heart rate, oxygen saturation, and temperature from the PC300Repository
        val sys = pC300Repository.sys.value
        val dia = pC300Repository.dia.value
        val hr = pC300Repository.heartRate.value
        val spo2 = pC300Repository.spO2.value
        val temp = pC300Repository.temp.value
        val glu = pC300Repository.glu.value

        // Set the value for the ECG file link to "Not-Performed"
        val ecgFile = "Not-Performed"

        // Get the user's location from the LocationRepository and format it to include the first six characters of the address and the postal code
        val location = locationRepository.userLocation.value
        var userLocation = ""
        if(location != null){
            userLocation = location.address+"/"+location.city+"/"+location.postalCode+"/"+location.country+"/"+location.lat+"/"+location.lon
        }
        // Generate a new UUID for the session and get the device ID, session date, and session time from the PC300Repository
        val sessionUUID = getSessionId()
        val pc303Device = MainActivity.pc300Repo.deviceId.takeLast(4)
        val omronDevice = MainActivity.omronRepo.deviceId.takeLast(4)
        val deviceId = pc303Device+omronDevice
        val sessionDate = pC300Repository.getSessionDate()
        val sessionTime = pC300Repository.getSessionTime()

        // Get the values for the three questions asked during the session from the PC300Repository and concatenate them into one string
        val answers = pC300Repository.answer1.value+pC300Repository.answer2.value+pC300Repository.answer3.value
        var bmi =  ""
        // If the latest weight info is not null, get the values for BMI and body fat percentage and convert
        if(weightInfo?.bmi != null){
            bmi = weightInfo.bmi.toString()
        }
        var bodyfat = ""
        if(weightInfo?.bodyFat != null){
            bodyfat = weightInfo.bodyFat.toString()
        }

        // replace BMI with glucose
//        newSessionPerformed.value = Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2,
//            bmi, bodyfat,temp, ecgFile,answers,"t",userLocation)

        newSessionPerformed.value = Session(sessionDate,sessionTime,sessionUUID,deviceId,subUserId, adminId, sys, dia, hr, spo2,
            glu, bodyfat,temp, ecgFile,answers,"t",userLocation)

        //create new session
        lastSavedSession = newSessionPerformed.value

        when(isCurrentSessionSaved){

            true -> {
                updateFullSession(newSessionPerformed.value)
            }

            false -> {
                createNewSession(newSessionPerformed.value)
            }
        }
    }

    /**
     * Updates the list of sessions for a sub user.
     *
     * @param list The updated list of sessions for the sub user.
     */
    fun updateSessionsResponseList(list: MutableList<Session>){
        Log.d("TAG", "updateSessionsResponseList: adding session")
        isSubUserSessionsList.value = list
//        isSubUserSessionsList.value = list.distinctBy { it.sessionId } as MutableList<Session>
//        if(bufferThere.value) {
//            Log.d("TAG", "updateSessionsResponseList: session added ${getSession()}")
//            list.add(getSession())
//        }
    }

    /**
     * Updates the boolean flag indicating whether or not session fetch has failed.
     *
     * @param isFailed The updated value for the boolean flag indicating whether or not session fetch has failed.
     */
    fun updateFailedFetch(isFailed : Boolean){
        isSessionFetchFailed.value = isFailed
    }

    /**
     * Updates the boolean flag indicating whether or not the session has been updated.
     *
     * @param isUpdated The updated value for the boolean flag indicating whether or not the session has been updated.
     */
    fun updateSessionUpdated(isUpdated : Boolean){
        isSessionUpdated.value = isUpdated
    }

    /**
     * Gets the list of sessions for a given user ID by making a network call to the APIManager class.
     * @param userId the ID of the user whose sessions are being retrieved
     * @return the list of sessions for the given user ID
     */
    fun getSessionsByUserID(userId : String) = APIManager.shared.getSessionByUserId(userId)

    /**
     * Creates a new session by making a network call to the APIManager class.
     * @param session the session to be created
     */
    fun createNewSession(session: Session) = APIManager.shared.createSession(session)

    /**
     * Updates a session by making a network call to the APIManager class.
     * @param session the session to be updated
     */
    fun updateSession(session: Session) = APIManager.shared.updateSessionBySessionId(session)

    fun updateFullSession(session: Session) = APIManager.shared.updateFullSession(session)

    /**
    Parses the medical history of a sub user and creates a list of SubUserMedicalHistory objects.
    @param user: SubUserProfile object whose medical history needs to be parsed.
    @return None.
     */
    fun parseUserMedicalHistory(user: SubUserProfile){
        // Split the medical history string into answers to individual questions.
        val medicalAnswers = user.medical_history.split(",")
        // Split each answer into question id and answer value and create a SubUserMedicalHistory object.
        val q1 = medicalAnswers[0].split(":")
        val q2 = medicalAnswers[1].split(":")
        val q3 = medicalAnswers[2].split(":")
        val q4 = medicalAnswers[3].split(":")
        val q5 = medicalAnswers[4].split(":")
        val q6 = medicalAnswers[5].split(":")
        val q7 = medicalAnswers[6].split(":")
        val answer1 = SubUserMedicalHistory(1, q1[0].toInt(), q1[1].toInt(), "")
        val answer2 = SubUserMedicalHistory(2, q2[0].toInt(), q2[1].toInt(), "")
        val answer3 = SubUserMedicalHistory(3, q3[0].toInt(), 0, "")
        val answer4 = SubUserMedicalHistory(4, q4[0].toInt(), 0, "")
        val answer5 = if(q5.size == 3){
            SubUserMedicalHistory(5, q5[0].toInt(), q5[1].toInt(), q5[2])
        }else{
            SubUserMedicalHistory(5, q5[0].toInt(), q5[1].toInt(), "")
        }
        val answer6 = SubUserMedicalHistory(5, q6[0].toInt(), 0, q6[1])
        val answer7 = SubUserMedicalHistory(5, q7[0].toInt(), 0, q7[1])
        // Create a list of SubUserMedicalHistory objects and update the subUserMedicalHistoryAnswer LiveData.
        var newList = mutableListOf(subUserMedicalHistoryAnswer)
        newList.add(0, answer1)
        newList.add(1, answer2)
        newList.add(2, answer3)
        newList.add(3, answer4)
        newList.add(4, answer5)
        newList.add(5, answer6)
        newList.add(6, answer7)
        updateSUbUserAnswersList(newList)
    }

    private fun updateSUbUserAnswersList(newList: MutableList<SubUserMedicalHistory>){
        isSubUserMedicalHistoryAnswersList.value = newList
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: SubUserDBRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SubUserDBRepository().also { instance = it }
            }
    }


    fun checkDouble(givenVal: String) : Double{
        try {
            return givenVal.toDouble()
        }catch (exception : NumberFormatException){
            return 0.0
        }
    }




    fun getadd(v1: String, v2: String, v3: String): Double {
        try {
            var count = 3
            val parsedV1 = if (v1.isNotEmpty()) v1.toDouble() else 0.0
            if (v1.isEmpty()) count -= 1
            val parsedV2 = if (v2.isNotEmpty()) v2.toDouble() else 0.0
            if (v2.isEmpty()) count -= 1
            val parsedV3 = if (v3.isNotEmpty()) v3.toDouble() else 0.0
            if (v3.isEmpty()) count -= 1
            val avg = (parsedV1 + parsedV2 + parsedV3) / count
            return avg
        } catch (e: NumberFormatException) {
            return 0.0
        }
    }

    fun calculateAvgSession(sessionList: List<Session>, selectedIndex : Int) {
        var totalSys = 0.0f
        var totalDia = 0.0f
        var totalHr = 0.0f
        var totalSpo2 = 0.0f
        var totalWeight = 0.0f
        var totalBodyFat = 0.0f
        var totalTemp = 0.0f
        var countSys = 0
        var countDia = 0
        var countHr = 0
        var countSpo2 = 0
        var countWeight = 0
        var countBodyFat = 0
        var countTemp = 0
        if(selectedIndex > 2){
            val removedsessionlist = sessionList.dropLast(sessionList.size - selectedIndex)
            Log.d("TAG", "calculateAvgSession: ")
            removedsessionlist.reversed().forEach { session ->
                val sys = session.sys.toFloatOrNull()
                if (sys != null && sys != 0.0f) {
                    if(countSys != 3){
                        totalSys += sys
                        countSys++
                    }
                }
                val dia = session.dia.toFloatOrNull()
                if (dia != null && dia != 0.0f) {
                    if(countDia != 3) {
                        totalDia += dia
                        countDia++
                    }
                }
                val hr = session.heartRate.replace(" bpm", "").toFloatOrNull()
                if (hr != null && hr != 0.0f) {
                    if(countHr != 3) {
                        totalHr += hr
                        countHr++
                    }
                }
                val spo2 = session.spO2.replace(" %", "").toFloatOrNull()
                if (spo2 != null && spo2 != 0.0f) {
                    if(countSpo2 != 3){
                        totalSpo2 += spo2
                        countSpo2++
                    }
                }
                val weight = session.weight.toFloatOrNull()
                if (weight != null && weight != 0.0f) {
                    if(countWeight != 3) {
                        totalWeight += weight
                        countWeight++
                    }
                }
                val bodyFat = session.bodyFat.toFloatOrNull()
                if (bodyFat != null && bodyFat != 0.0f) {
                    if(countBodyFat != 3) {
                        totalBodyFat += bodyFat
                        countBodyFat++
                    }
                }
                val temp = session.temp.replace(" °C", "").toFloatOrNull()
                if (temp != null && temp != 0.0f) {
                    if(countTemp != 3) {
                        totalTemp += temp
                        countTemp++
                    }
                }
            }
            val avgSys = if (countSys == 3) totalSys / countSys else 0.0f
            val avgDia = if (countDia == 3) totalDia / countDia else 0.0f
            val avgHr = if (countHr == 3) totalHr / countHr else 0.0f
            val avgSpo2 = if (countSpo2 == 3) totalSpo2 / countSpo2 else 0.0f
            val avgWeight = if (countWeight == 3) totalWeight / countWeight else 0.0f
            val avgBodyFat = if (countBodyFat == 3) totalBodyFat / countBodyFat else 0.0f
            val avgTemp = if (countTemp == 3) totalTemp / countTemp else 0.0f
            lastAvgSession =  Session("","","","","","",
                avgSys.toInt().toString(),
                avgDia.toInt().toString(), avgHr.format(1),
                avgSpo2.format(1), avgWeight.format(1), avgBodyFat.format(1),
                avgTemp.format(1), "", "", "", "")
        }else{
            lastAvgSession =  Session("","","","","","", "", "", "", "", "", "", "", "", "", "", "")
        }
    }
}
// Extension function to format float to 1 decimal place
fun Float.format(digits: Int) = "%.${digits}f".format(this)