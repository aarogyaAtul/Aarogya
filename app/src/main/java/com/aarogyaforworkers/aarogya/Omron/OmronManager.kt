package com.aarogyaforworkers.aarogya.Omron
import android.content.Context
import android.util.Log
import com.aarogyaforworkers.aarogya.MainActivity
import net.huray.omronsdk.OmronDeviceManager
import net.huray.omronsdk.ble.entity.WeightDeviceInfo
import net.huray.omronsdk.ble.enumerate.OHQSessionType
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey
import net.huray.omronsdk.ble.enumerate.OmronDeviceType

class OmronManager {

    private var omronDeviceType: OmronDeviceType? = null

    var userIndex = 1

    var latestIncremenetKey : Long = 29

    var sequenceNumber = -1

    var omronConnectionManager : OmronDeviceManager? = null

    var omronTransferManager : OmronDeviceManager? = null

    var deviceInfo : WeightDeviceInfo? = null

    var omronStatus = 0

    var lastFetchedUser : Map<OHQUserDataKey, Any>? = null

    var lastWeightIndex = 0

    /**
     * This function initializes the Omron device registration and transfer managers.
     * @param context The context of the application.
     */
    fun register(context: Context){
        // Set the Omron device type to the first device in the list.
        omronDeviceType = OmronDeviceType.fromId(0)
        // Initialize the Omron device connection manager with the specified context, device category, session type, and listener.
        omronConnectionManager = OmronDeviceManager(context, omronDeviceType!!.category, OHQSessionType.REGISTER, OmronConnectionListner())
        // Initialize the Omron device transfer manager with the specified context, device category, session type, and listener.
        omronTransferManager = OmronDeviceManager(context, omronDeviceType!!.category, OHQSessionType.TRANSFER, OmronTransferListner())
    }

    fun requestPermission(context: Context){
        omronConnectionManager?.requestBluetoothPermissions(context)
    }



    /**
     * This function starts scanning for Omron devices with the specified device type.
     */
    fun startScan(){
        // Create a list of device types to scan for.
        val targets = listOf(omronDeviceType)
        // Start scanning for devices with the specified device types using the Omron connection manager.
        omronConnectionManager!!.startScan(targets)
    }

    /**
     * This function stops scanning for Omron devices.
     */
    fun stopScan(){
        // Stop scanning for devices using the Omron connection manager.
        omronConnectionManager!!.stopScan()
    }

    /**
     * This function connects to an Omron weight device with the specified device address and user index.
     * @param deviceAddress The address of the device to connect to.
     */
    fun connectDevice(deviceAddress : String){
        // Create a new weight device info object with the specified user ID, device address, and user index.
        deviceInfo = WeightDeviceInfo.newInstanceForRegister(Const.demoUser, deviceAddress, userIndex)
        // Connect to the weight device using the Omron connection manager.
        omronConnectionManager!!.connectWeightDevice(deviceInfo)
    }

    /**
     * This function disconnects from the connected Omron device.
     */
    fun disconnectDevice() {
        // Cancel the current session with the Omron device using the Omron connection manager.
        omronConnectionManager!!.cancelSession()
    }

    /**
     * This function requests the last session data from the connected Omron weight device.
     * @param user A map containing the user information.
     */
    fun requestLastSessionDataInfo(user : Map<OHQUserDataKey, Any>?){
        // Check if user information is not null.
        if(user != null){
            // Create a new weight device info object with the user information, device address, user index, sequence number, and latest increment key.
            deviceInfo = WeightDeviceInfo.newInstanceForTransfer(user, MainActivity.omronRepo.connectedOmronDevice.value!!.address, userIndex, sequenceNumber, latestIncremenetKey)
            // Request the last session data from the weight device using the Omron transfer manager.
            omronTransferManager!!.requestWeightData(deviceInfo)

            Log.d("TAG", "requestLastSessionDataInfo: syncing for user $user")
        }
    }

    fun syncAndFetchDataForUser(user: Map<OHQUserDataKey, Any>?, seqkey : Int, incDeckey: Long){
        deviceInfo = WeightDeviceInfo.newInstanceForTransfer(user, MainActivity.omronRepo.connectedOmronDevice.value!!.address, 1, seqkey, incDeckey)
        // Request the weight data for the selected user from the weight device using the Omron transfer manager.
        omronTransferManager!!.requestWeightData(deviceInfo)
    }


    /**
     * This companion object provides a shared instance of the OmronManager class.
     * This makes it easy to access the shared instance from other classes without having to create a new instance.
     */
    companion object{
        // Create a shared instance of the OmronManager class.
        val shared = OmronManager()
    }
}

