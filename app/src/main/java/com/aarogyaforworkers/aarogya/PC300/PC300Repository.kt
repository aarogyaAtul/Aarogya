package com.aarogyaforworkers.aarogya.PC300

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive
import com.aarogyaforworkers.awsapi.models.Session

class PC300Repository {

    var deviceId = "XXXX"
    var isEcgDataTaken = false
    var sessionDate : MutableState<String> = mutableStateOf("")
    var sessionTime : MutableState<String> = mutableStateOf("")
    var isOnSessionPage  = false
    private var isSessionPerformed : MutableState<Boolean> = mutableStateOf(false)
    private var isConnectionStatus : MutableState<Boolean> = mutableStateOf(false)
    private var isPC300ConnectionStatus : MutableState<Boolean> = mutableStateOf(false)
    private var isPC300DisconnectionStatus : MutableState<Boolean> = mutableStateOf(false)
    private var isBloodPressure : MutableState<String> = mutableStateOf("")
    private var isTemp : MutableState<String> = mutableStateOf("")
    private var isSpo2 : MutableState<String> = mutableStateOf("")
    private var isHeartRate : MutableState<String> = mutableStateOf("")
    private var isSys : MutableState<String> = mutableStateOf("")
    private var isDia : MutableState<String> = mutableStateOf("")
    private var isECGValue : MutableState<Int> = mutableStateOf(5)
    private var isECGResultCode : MutableState<Int> = mutableStateOf(0)
    private var isGlu : MutableState<String> = mutableStateOf("")
    private var isConnectedPC300Device : MutableState<BluetoothDevice?> = mutableStateOf(null)
    private var isConnectedOmronDevice : MutableState<BluetoothDevice?> = mutableStateOf(null)
    private var isDeviceList : MutableState<ArrayList<BluetoothDevice>?> = mutableStateOf(null)
    var deviceList : State<ArrayList<BluetoothDevice>?> = isDeviceList
    val dataPoint = DataPoint(0f, -1f)
    var currentDatapoint : MutableState<DataPoint> = mutableStateOf(dataPoint)

    var isShowEcgRealtimeAlert : MutableState<Boolean> =  mutableStateOf(false)

    val showEcgRealtimeAlert : State<Boolean> = isShowEcgRealtimeAlert


    var currentDataPointState : State<DataPoint> = currentDatapoint

    var iscurrentDrawState : MutableState<Boolean> = mutableStateOf(false)

    var drawState : State<Boolean> = iscurrentDrawState

    var canvasSize : IntSize = IntSize.Zero

    var stepx =  0f

    var zoomECGforMm = 0f

    var heightMm = 0f

    var weight = 0

    var height = 0

    var dm = 0f

    var yPX2MMUnit = 0f


    val dataToDraw = mutableStateListOf<Float>()

    fun cleanDisplayBuffer(){
        StaticReceive.DRAWDATA.clear()
        dataToDraw.clear()
    }


    fun setUpECGScreenConfiguration(context: Context){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        weight = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        zoomECGforMm = (height/ 114.3f)
        dm = displayMetrics.density
        yPX2MMUnit = 25.4f/displayMetrics.densityDpi
        heightMm = height * yPX2MMUnit
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        dm = displayMetrics.density
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun updateDataPoint(x: Float, y : Float){
        val newData = DataPoint(x, y)
        currentDatapoint.value = newData
    }

    var temp : State<String> = isTemp
    var glu : State<String> = isGlu
    var spO2 : State<String> = isSpo2
    var bloodPressure : State<String> = isBloodPressure
    var heartRate : State<String> = isHeartRate
    var connectionStatus : State<Boolean> = isConnectionStatus
    var pc300connectionStatus : State<Boolean> = isPC300ConnectionStatus
    var ecg : State<Int> = isECGValue
    var ecgResultCode : State<Int> = isECGResultCode
    var sys : State<String> = isSys
    var dia : State<String> = isDia
    var isSessionStarted : State<Boolean> = isSessionPerformed
    var connectedPC300Device : State<BluetoothDevice?> = isConnectedPC300Device

    var answer1 : MutableState<String> = mutableStateOf("Y")
    var answer2 : MutableState<String> = mutableStateOf("X")
    var answer3 : MutableState<String> = mutableStateOf("X")

    var isBpTimerStarted = false
    private val bpTimeout : Long = 70000
    private val ecgTimeout : Long = 55000

    var isEcgTimerStarted = false

    val handler = Handler()

    val ecgHandler = Handler()

    val bpHandler = Handler()

    fun getEcgResultMsgBasedOnCode(context: Context) : String{
        val result = context.resources.getStringArray(R.array.ecg_measureres)
        return result[MainActivity.pc300Repo.ecgResultCode.value]
    }

    var ecgTimer: Runnable? = null

    var bpTimer: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkEcgTimeOut() {
        Log.e("tag", "OnGetDataMode ECG timer ")

        if (!isEcgTimerStarted) {
            isEcgTimerStarted = true
            Log.e("tag", "OnGetDataMode ECG timer started")
            ecgTimer = Runnable {
                if (ecg.value != 2) {
                    updateEcgResult(3)
                }
                Log.e("tag", "OnGetDataMode ECG timer stopping")
                stopEcgTimer()
            }
            ecgHandler.postDelayed(ecgTimer!!, ecgTimeout)
        }
    }

    fun stopEcgTimer() {
        if (ecgTimer != null) {
            ecgHandler.removeCallbacks(ecgTimer!!)
            ecgTimer = null
            isEcgTimerStarted = false
            Log.e("tag", "OnGetDataMode ECG timer stopped")
        }
    }
    fun checkBpTimeOut() {
        if (!isBpTimerStarted) {
            Log.e("tag", "OnGetDataMode BP timer started")

            isBpTimerStarted = true
            bpTimer = Runnable {
                if (bloodPressure.value.length <= 3) {
                    updateBloodPressure("e")
                }
                isBpTimerStarted = false
                stopBpTimer()
                Log.e("tag", "OnGetDataMode BP timer stopping")
            }
            handler.postDelayed(bpTimer!!, bpTimeout)
        }
    }

    fun stopBpTimer() {
        if (bpTimer != null) {
            handler.removeCallbacks(bpTimer!!)
            bpTimer = null
            isBpTimerStarted = false
            Log.e("tag", "OnGetDataMode BP timer stopped")

        }
    }

    /**
     * Updates the answer for the given question number in the ongoing session.
     *
     * @param questionNo The question number to update the answer for.
     * @param questionAnswer The new answer for the given question number.
     */
    fun updateOnGoingSessionQuestionAnswers(questionNo : Int, questionAnswer : String){
        when(questionNo){
            1 -> {
                answer1.value = questionAnswer // Update answer1 with the new answer.
            }

            2 -> {
                answer2.value = questionAnswer // Update answer2 with the new answer.
            }

            3 -> {
                answer3.value = questionAnswer // Update answer3 with the new answer.
            }
        }
    }

    /**
     * Updates the date of the session.
     *
     * @param date The new date for the session.
     */
    fun updateSessionDate(date : String){
        // Update the sessionDate value with the new date.
        sessionDate.value = date
    }

    /**
     * Updates the time value for the session.
     *
     * @param time The new time value for the session.
     */
    fun updateSessionValue(time : String){
        // Update the sessionTime value with the new time.
        sessionTime.value = time
    }

    /**
     * Connects to a PC-300 Bluetooth device.
     *
     * @param device The Bluetooth device to connect to.
     */
    fun connectPC300(device: BluetoothDevice){
        // Use the Pc300Manager singleton to connect to the specified device.
        Pc300Manager.shared.connectDevice(device)
    }

    /**
     * Updates the connection status of the PC-300 device.
     *
     * @param isConnected Whether or not the device is connected.
     */
    fun updatePC300ConnectionStatus(isConnected: Boolean){
        // Update the isPC300ConnectionStatus value with the new connection status.
        isPC300ConnectionStatus.value = isConnected
    }

    /**
     * Updates the currently connected PC-300 device.
     *
     * @param device The Bluetooth device that is currently connected.
     */
    fun updateConnectedPC300Device(device: BluetoothDevice){
        // Update the isConnectedPC300Device value with the newly connected device.
        isConnectedPC300Device.value = device
    }

    /**
     * Scans for nearby PC-300 devices and updates the device list.
     */
    fun scanPC300Device(){
        // Set the device list to null to reset it before starting the scan.
        isDeviceList.value = null
        // Use the Pc300Manager singleton to scan for nearby devices.
        Pc300Manager.shared.scanDevice()
    }

    /**
     * Disconnects from the currently connected PC-300 device.
     */
    fun disConnectPC300Device(){
        // Use the Pc300Manager singleton to disconnect from the PC-300 device.
        Pc300Manager.shared.disconnectPC300()
    }

    /**
     * Initializes the PC-300 Bluetooth device.
     *
     * @param context The application context.
     */
    fun initializePC300(context : Context){
        // Use the Pc300Manager singleton to initialize the PC-300 device.
        Pc300Manager.shared.initializePC300(context)
    }

    /**
     * Updates the date and time on the connected PC-300 device.
     * Requires API level 26 or higher.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDateTime(){
        // Use the Pc300Manager singleton to update the date and time on the device.
        Pc300Manager.shared.updateDateTime()
        sessionAllreadySaved = false
        isSessionSaved = false
    }

    /**
     * Returns the date for the current session.
     *
     * @return The date for the current session.
     */
    fun getSessionDate() : String{
        // Return the value of the sessionDate LiveData object.
        return sessionDate.value
    }

    /**
     * Returns the time for the current session.
     *
     * @return The time for the current session.
     */
    fun getSessionTime() : String{
        // Return the value of the sessionTime LiveData object.
        return sessionTime.value
    }

    /**
     * Updates the connection status of the device.
     *
     * @param isConnected Whether or not the device is connected.
     */
    fun updateConnectionStatus(isConnected : Boolean){
        // Update the isConnectionStatus value with the new connection status.
        isConnectionStatus.value = isConnected
    }

    fun updateEcgResultCode(code : Int){
        isECGResultCode.value = code
    }

//    fun getResultBasedOnEcgResultCode() : String{
//        val result = MainActivity.shared.resources.getStringArray(R.array.ecg_measureres)
//        return result[ecgResultCode.value]
//    }

    /**
     * Updates the temperature value and adds it to the session history.
     * Requires API level 26 or higher.
     *
     * @param value The new temperature value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTempValue(value : String){
        // Update the isTemp value with the new temperature value.
        isTemp.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new temperature value to the session history.
        addTempSession(value)
    }

    fun updateGluValue(value : String){
        // Update the isTemp value with the new temperature value.
        isGlu.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new temperature value to the session history.
    }

    /**
     * Updates the SpO2 value and adds it to the session history.
     * Requires API level 26 or higher.
     *
     * @param value The new SpO2 value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSpO2(value: String){
        // Update the isSpo2 value with the new SpO2 value.
        isSpo2.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new SpO2 value to the session history.
        addSpo2Session(value)
    }

    /**
     * Updates the heart rate value and adds it to the session history.
     * Requires API level 26 or higher.
     *
     * @param value The new heart rate value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateHR(value: String){
        // Update the isHeartRate value with the new heart rate value.
        isHeartRate.value = value

        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new heart rate value to the session history.
        addHRSession(value)
    }

    /**
     * Updates the blood pressure value and sets the isSessionPerformed flag to true.
     *
     * @param value The new blood pressure value.
     */
    fun updateBloodPressure(value: String){
        // Update the isBloodPressure value with the new blood pressure value.
        isBloodPressure.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
    }

    /**
     * Updates the systolic value of the blood pressure and adds it to the session history.
     * Requires API level 26 or higher.
     *
     * @param value The new systolic value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSys(value: String){
        // Update the isSys value with the new systolic value.
        isSys.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new systolic value to the session history.
        addSYSSession(value)
    }

    /**
     * Updates the diastolic value of the blood pressure and adds it to the session history.
     * Requires API level 26 or higher.
     *
     * @param value The new diastolic value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDia(value: String){
        // Update the isDia value with the new diastolic value.
        isDia.value = value
        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Add the new diastolic value to the session history.
        addDIASession(value)
    }

    /**
     * Updates the ECG value and sets the isSessionPerformed flag to true.
     * If the ECG value is 0, creates a new ECG file for the session using the CSVRepository and
     * the sub-user session ID. If the ECG data is taken and the session file exists and the ECG
     * value is 2, uploads the session file to S3 using the S3Repository.
     * Requires API level 26 or higher.
     *
     * @param value The new ECG value.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEcgResult(value: Int){
        // Update the isSessionPerformed value to indicate that a session has been performed.
        isSessionPerformed.value = true
        // Update the isECGValue with the new ECG value.

//        if(value != 2) isECGValue.value = value

        isECGValue.value = value

        MainActivity.subUserRepo.updateIsBufferThere(true)
        // Set the isEcgDataTaken flag to true.
        isEcgDataTaken = true
        // If not on session page, check if a new ECG file needs to be created or uploaded to S3.
        if(!isOnSessionPage){
            // If the ECG value is 0, create a new ECG file for the session using the CSVRepository and the sub-user session ID.
            if(value == 0){
                createNewECGFile(MainActivity.csvRepository, MainActivity.subUserRepo.getSessionId())
            }
            // If the ECG data is taken and the session file exists and the ECG value is 2, upload the session file to S3 using the S3Repository.
            if(isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null){
               if(value == 2){
                   MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
                }
            }
        }
    }

    // Initialize defGuestID with the admin ID of the logged-in user.
    private var defGuestID = MainActivity.adminDBRepo.getLoggedInUser().admin_id


    // Add Temp session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addTempSession(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, "","","","","","",value,"", "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    // Add SYS session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addSYSSession(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, value,"","","","","","","", "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    // Add DIA session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDIASession(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, "",value,"","","","","","", "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    // Add HR session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    fun addHRSession(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, "","",value,"","","","","", "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    // Add ECG session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    fun addEcgSession(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, "","","","","","","",value, "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    // Add Spo2 session to guest database
    @RequiresApi(Build.VERSION_CODES.O)
    fun addSpo2Session(value: String){
        // If on session page, do not add new session
        if(isOnSessionPage) return
        // Update current date and time
        updateDateTime()
        // Get admin user details
        val admin = MainActivity.adminDBRepo.getLoggedInUser()
        // Generate default guest ID using admin user details
        defGuestID = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')
        // Generate session ID using session time, device ID, and admin user ID
        val sid = sessionTime.value+":"+deviceId.takeLast(4)+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id
        // Create new Session object with relevant data
        val insertSession = Session(sessionDate.value, sessionTime.value, sid, deviceId, defGuestID, MainActivity.adminDBRepo.getLoggedInUser().admin_id, "","","",value,"","","","", "","t",
            MainActivity.locationRepo.userLocation.value?.postalCode.toString())
        // Add session to guest database
        MainActivity.subUserRepo.createNewSession(insertSession)
    }

    /**
     * Updates the device ID with the provided string value.
     * @param id The new device ID to be set.
     */
    fun updateDeviceId(id : String){
        deviceId = id
    }

    /**
     * Updates the device list with the provided ArrayList of Bluetooth devices.
     * @param list The new list of devices to be set.
     */
    fun updateDeviceList(list : ArrayList<BluetoothDevice>?){
        isDeviceList.value = list
    }

    /**
     * Creates a new ECG file with the given session ID by updating the file name in the CSV repository.
     * @param csvRepository The CSV repository to update the file name in.
     * @param sessionId The ID of the ECG session to create a new file for.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewECGFile(csvRepository: CsvRepository, sessionId : String){
        csvRepository.updateFileName(sessionId)
    }

    /**
     * Clears the values of all variables related to a session.
     * This includes clearing values for blood pressure, temperature, SpO2, heart rate, systolic and diastolic values,
     * ECG value, session performed status, session started status, and answers to three questions.
     * Additionally, it resets the weight info in the Omron repository and updates variables that store these values.
     */
    fun clearSessionValues(){
        isBloodPressure.value = ""
        isTemp.value = ""
        isSpo2.value = ""
        isGlu.value = ""
        isHeartRate.value = ""
        isSys.value = ""
        isDia.value = ""
        MainActivity.omronRepo.resetWeightInfo()
        isECGValue.value = 5
        temp = isTemp
        glu = isGlu
        spO2 = isSpo2
        bloodPressure = isBloodPressure
        heartRate = isHeartRate
        sys = isSys
        dia = isDia
        ecg = isECGValue
        isSessionPerformed.value = false
        isSessionStarted = isSessionPerformed
        isEcgDataTaken = false
        answer1.value = "Y"
        answer2.value = "X"
        answer3.value = "X"
        isDeleting = false
    }

    /**
     * Clears the variables related to the PC300 device.
     * This includes clearing the variable for the connected PC300 device, updating the connection status and PC300 connection status.
     */
    fun clearPC300(){
        isConnectedPC300Device.value = null
        connectedPC300Device = isConnectedPC300Device
        updateConnectionStatus(false)
        updatePC300ConnectionStatus(false)
    }

    /**
     * Updates the PC300 disconnection status with the provided boolean value.
     * @param isDisconnected The boolean value to update the PC300 disconnection status with.
     */
    fun updatePc300DisconnectionStatus(isDisconnected: Boolean){
        isPC300DisconnectionStatus.value = isDisconnected
    }


    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: PC300Repository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PC300Repository().also { instance = it }
            }
    }
}