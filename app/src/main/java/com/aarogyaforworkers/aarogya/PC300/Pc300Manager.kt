package com.aarogyaforworkers.aarogya.PC300

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive
import com.creative.base.BaseDate.Wave
import com.creative.bluetooth.BluetoothOpertion
import com.creative.bluetooth.IBluetoothCallBack
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Pc300Manager(repository: PC300Repository) {

    private val tag = "PC300Manager"

    var bleOperator : BluetoothOpertion? = null

    var conDeviceName = -1

    private var deviceList : ArrayList<BluetoothDevice> = ArrayList()

    private var ecgData : MutableState<ArrayList<Int>> = mutableStateOf(ArrayList())

    var isEcgData : State<ArrayList<Int>> = ecgData

    fun addECgData(){
        val data: Wave = StaticReceive.DRAWDATA.removeAt(0)
        isEcgData.value?.add(data.data)
    }

    fun clearECgData(){
        StaticReceive.DRAWDATA.clear()
        MainActivity.pc300Repo.dataToDraw.clear()
        ecgData.value = ArrayList()
    }

    private var isInitialized = false

    // The variable "bluetoothArray" is a 2D array containing device names.Used to check if a device is one of the supported Bluetooth devices.
    private val bluetoothArray = arrayOf(
        arrayOf(
            "PC_300SNT", "PC-200", "QC-200",
            "PC-100"
        )
    )

    // The "bleSocket" variable is a BluetoothSocket object and is used to store the connection to the Bluetooth device.
    var bleSocket : BluetoothSocket? = null

    // The "initializePC300" function initializes a Bluetooth operation by creating a new instance of the BluetoothOperation class, which takes a context and a callback function as arguments. The callback function is defined elsewhere.
    fun initializePC300(context: Context){
        if(isInitialized) return
        bleOperator = BluetoothOpertion(context, pc300ConnectionCallBack)
        isInitialized = true
    }

    // The "connectDevice" function connects to a Bluetooth device by calling the "connect" function on the BluetoothOperator object, which takes a BluetoothDevice object as an argument. The "conDeviceName" variable is set to -1, although it is not clear what this variable is used for.
    fun connectDevice(device: BluetoothDevice){
        bleOperator?.connect(device)
        conDeviceName = -1
    }

    // This function starts scanning for nearby Bluetooth devices using the BluetoothOperator instance and stores the list of devices found in deviceList. conDeviceName is set to 0.
    fun scanDevice() {
        conDeviceName = 0
        // Create a new ArrayList for storing the devices found
        deviceList = ArrayList()
        // Call the discovery() method of the BluetoothOperator instance to start scanning for nearby devices
        bleOperator?.discovery()
    }

    fun disconnectPC300() {
        bleOperator?.disConnect(bleSocket)
    }

    /**
     * This function updates the date and time of the current session in the PC300Repo by getting the current
       date and time using LocalDateTime and formatting it using DateTimeFormatter. It then updates the session
       date and value in the PC300Repo using MainActivity
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDateTime(){
        // Get the current date and time using LocalDateTime
        val currentDateTime = LocalDateTime.now()
        // Define the date and time formatters using DateTimeFormatter
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        // Update the session date and value in the PC300Repo using MainActivity
        MainActivity.pc300Repo.updateSessionDate(currentDateTime.toLocalDate().format(dateFormatter).toString())
        MainActivity.pc300Repo.updateSessionValue(currentDateTime.toLocalTime().format(timeFormatter).toString())
    }

    /**
     * This function checks if the given Bluetooth device name matches any of the predefined device names in the `bluetoothArray` array.
       @param name The name of the Bluetooth device to check.
       @param dev The index of the device type in the `bluetoothArray` array.
       @return `true` if the device name matches any of the predefined device names, `false` otherwise.
     */
    fun checkName(name: String, dev: Int): Boolean {
        if (!TextUtils.isEmpty(name) && dev < bluetoothArray.size) {
            for (i in bluetoothArray[dev].indices) {
                if (name == (bluetoothArray[dev][i])) return true
            }
        }
        return false
    }

    private var pc300ConnectionCallBack: IBluetoothCallBack = object : IBluetoothCallBack{
        /**
         * Scanned Results. Called when a Bluetooth device is found during a discovery scan
         * */
        @SuppressLint("MissingPermission")
        override fun onFindDevice(p0: BluetoothDevice?) {
            // Get the name of the device found
            val name: String? = p0?.name
            // Check if the device name is not null and we are looking for a device (-1 indicates we are not looking for a device)
            if(name != null && conDeviceName != -1){
                Log.d("TAG", "onFindDevice: ${p0.address}")
                // Check if the name of the device contains the name of a device we are looking for
                if(checkName(name, conDeviceName)){
                    // If the device is not already in the device list, add it
                    if(!deviceList.contains(p0)) deviceList.add(p0)
                    // Update the device list in the repository
                    repository.updateDeviceList(deviceList)
                }
            }
        }

        // Called when the discovery scan has completed
        override fun onDiscoveryCompleted(p0: MutableList<BluetoothDevice>?) {
            Log.d(tag, "onDiscoveryCompleted: ")
        }

        // Called when a connection to a Bluetooth device has been established
        override fun onConnected(p0: BluetoothSocket?) {
            // Set the Bluetooth socket to the one passed in
            bleSocket = p0
            // Initialize the PC300 receiver
            PC300Receiver.shared.initializeReceiver()
            // Update the connection status in the repository
            repository.updateConnectionStatus(true)
            // Update the device ID in the repository
            repository.updateDeviceId(bleSocket?.remoteDevice?.address.toString().replace(":", "").takeLast(4))
            Log.e(tag, "onConnected: ${p0?.isConnected}")
            // Update the connected PC300 device in the repository
            repository.updateConnectedPC300Device(p0!!.remoteDevice)
            // Update the PC300 connection status in the repository
            repository.updatePC300ConnectionStatus(true)
            // Update the PC300 disconnection status in the repository
            repository.updatePc300DisconnectionStatus(false)
        }

        // Called when a connection to a Bluetooth device fails
        override fun onConnectFail(p0: String?) {
            // Set the Bluetooth socket to null
            bleSocket = null
            // Update the connection status in the repository
            repository.updateConnectionStatus(false)
            // Update the PC300 disconnection status in the repository
            repository.updatePc300DisconnectionStatus(true)
            // Update the PC300 connection status in the repository
            repository.updatePC300ConnectionStatus(false)
            Log.e(tag, "onConnectFail: $p0")
        }

        // Called when an exception occurs during a Bluetooth operation
        override fun onException(p0: Int) {
            // Set the Bluetooth socket to null
            bleSocket = null
            // Update the connection status in the repository
            repository.updateConnectionStatus(false)
            // Update the PC300 connection status in the repository
            repository.updatePC300ConnectionStatus(false)
            Log.e(tag, "onException: $p0")
        }

        override fun onConnectLocalDevice(p0: BluetoothSocket?) {
            Log.d(tag, "onConnectLocalDevice: ${p0?.isConnected}")
        }

    }

    companion object{
        val shared = Pc300Manager(MainActivity.pc300Repo)
    }

}


