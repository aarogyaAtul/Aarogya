package com.aarogyaforworkers.aarogya.Omron

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.Commons.isOnUserHomeScreen
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.storage.OmronPreferencemanager
import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.ble.enumerate.OHQGender
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey
import net.huray.omronsdk.utility.Handler
import java.math.BigDecimal
import java.util.HashMap
import java.util.Timer
import java.util.TimerTask

class OmronRepository {

    private var isDeviceList : MutableState<ArrayList<DiscoveredDevice>?> = mutableStateOf(null)

    private var lastIncDecKey : Long = 0

    private var lastSeqKey : Int = 0

    fun setLastIncDecKey(key : Long){
        lastIncDecKey = key
    }

    var deviceId = "XXXX"

    fun setLastSeqNumKey(key: Int){
        lastSeqKey = key
    }

    fun getLastIncDecKey() : Long = lastIncDecKey

    fun getLastSeqKey() : Int = lastSeqKey

    var deviceList : State<ArrayList<DiscoveredDevice>?> = isDeviceList

    var isAllreadyWeightIndexSynced = false

    var isDeviceStatus : MutableState<String> = mutableStateOf("")

    var deviceStat : State<String> = isDeviceStatus

    fun updateDeviceStatus(status : String){
        isDeviceStatus.value = status
    }

    var isDeviceConnected = false

    private var isOmronRegistrationFailed : MutableState<Boolean?> = mutableStateOf(null)

    private var isOmronLastUserUpdated : MutableState<Boolean> = mutableStateOf(false)

    fun updateOmronLastUserUpdatedStatus(isUdpated : Boolean){
        isOmronLastUserUpdated.value = isUdpated
    }

    var omronRegistrationFailed : State<Boolean?> = isOmronRegistrationFailed

    private var isOmronRegistrationStatus : MutableState<Int> = mutableStateOf(6)

    private var isSessionPerformed : MutableState<Boolean> = mutableStateOf(false)

    var isSessionStarted : State<Boolean> = isSessionPerformed

    private var isLatestUserWeightInfo : MutableState<UserWeightInfo?> = mutableStateOf(null)

    var latestUserWeightInfo : State<UserWeightInfo?> = isLatestUserWeightInfo

    private var isConnectedOmronDevice : MutableState<DiscoveredDevice?> = mutableStateOf(null)

    var connectedOmronDevice : State<DiscoveredDevice?> = isConnectedOmronDevice

    private var isOmronConnectionStatus : MutableState<Boolean> = mutableStateOf(false)

    var omronConnectionStatus : State<Boolean> = isOmronConnectionStatus

    fun register(context: Context){
        // Initialize the OmronDeviceType object.
        OmronManager.shared.requestPermission(context)
        OmronManager.shared.register(context)
    }

    fun startScan(){
        // Reset the device state.
        isAllreadyWeightIndexSynced = false
        resetOmronDevice()
        OmronManager.shared.startScan()
    }

    // Connect to the Omron device.
    fun connectOmronDevice(device: DiscoveredDevice){
        OmronManager.shared.connectDevice(device.address)
        // Update the connected Omron device in the repository.
        updateConnectedOmronDevice(device)
        deviceId = device.address.replace(":", "".takeLast(4))
    }


    fun disconnectDevice(){
        OmronManager.shared.disconnectDevice()
    }

    // update new updated weight state
    fun updateLatestUserWeight(weightInfo: UserWeightInfo){
        isLatestUserWeightInfo.value = weightInfo
        updateSessionPerformed(true)
        MainActivity.subUserRepo.updateIsBufferThere(true)
    }

    // reset previous stored weight on fetching new weight
    fun resetWeightInfo(){
        isLatestUserWeightInfo.value = null
        updateSessionPerformed(false)
    }

    // track where session is performed or not
    private fun updateSessionPerformed(isPerformed : Boolean){
        isSessionPerformed.value = isPerformed
    }

    // get the sequence key of selected patient
    fun getLastSeqIncrementKeyForSelectedUser(){
        OmronManager.shared.omronStatus = 0
        requestLastSessionDataInfo(getSelectedPatient())
    }

    // sync patient weight to omron device and update it's status
    fun syncUserDataToDevice(){
        OmronManager.shared.omronStatus = 1
        requestLastSessionDataInfo(getSelectedPatient())
    }

    var isReadyForFetch = false

    var isAllReadyExecuting = false

    fun setUserProfile(){
        Log.d("TAG", "onTransferSuccess: it is syncing profile ${getSelectedPatient()}")
        if(!isAllReadyExecuting && isOnUserHomeScreen) {
            OmronManager.shared.syncAndFetchDataForUser(getSelectedPatient(), getLastSeqKey(), getLastIncDecKey())
            isAllReadyExecuting = true
        }
    }

    fun fetchUserProfile(){
        Log.d("TAG", "onTransferSuccess: all ready synced ${getSelectedPatient()}")
        if(isOnUserHomeScreen) OmronManager.shared.syncAndFetchDataForUser(getSelectedPatient(), getLastSeqKey(), getLastIncDecKey())
    }

    // fetch user sequence key and databaseChangeIncrement key from last session info
    private fun requestLastSessionDataInfo(user : Map<OHQUserDataKey, Any>?){
        OmronManager.shared.requestLastSessionDataInfo(user)
    }

    // update scanned omron device list
    fun updateDeviceList(deviceList: List<DiscoveredDevice>){
        val array = ArrayList<DiscoveredDevice>()
        for (d in deviceList){
            array.add(d)
        }
        isDeviceList.value = array
    }

    // update the connected omron device
    private fun updateConnectedOmronDevice(device: DiscoveredDevice){
        isConnectedOmronDevice.value = device
    }

    // update the connection status of omron device
    fun updateOmronConnectionStatus(isConnected : Boolean){
        isOmronConnectionStatus.value = isConnected
    }

    // reset omron device on reconnection
    fun resetOmronDevice(){
        isOmronRegistrationFailed.value = null
        isConnectedOmronDevice.value = null
        isOmronConnectionStatus.value = false
        isDeviceList.value = null
    }


    // get the patient object for selected patient
    fun getSelectedPatient(): Map<OHQUserDataKey, Any>?{
       updateOmronLastUserUpdatedStatus(false)
        val user = MainActivity.adminDBRepo.getSelectedSubUserProfile()
        val dob = user.dob.split("/")
        if(dob.size == 2){
            val dobM = dob[0].toInt() + 1
            val dobY = dob[1]
            val userDOb = dobY +"-"+ "%02d".format(dobM) + "-01"
            val height = user.height.toString()
            var gender = OHQGender.Male
            if(user.gender == "Male" || user.gender == "male") gender = OHQGender.Male
            if(user.gender == "Female" || user.gender == "female") gender = OHQGender.Female
            val userData: MutableMap<OHQUserDataKey, Any> = HashMap()
            userData[OHQUserDataKey.DateOfBirthKey] = userDOb
            userData[OHQUserDataKey.HeightKey] = BigDecimal(height)
            userData[OHQUserDataKey.GenderKey] = gender
            return userData
        }
        return null
    }


    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: OmronRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: OmronRepository().also { instance = it }
            }
    }

}