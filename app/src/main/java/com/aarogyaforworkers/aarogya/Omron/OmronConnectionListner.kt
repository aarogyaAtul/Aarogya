package com.aarogyaforworkers.aarogya.Omron

import android.util.Log
import com.aarogyaforworkers.aarogya.MainActivity
import net.huray.omronsdk.OmronDeviceManager.RegisterListener
import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.ble.entity.SessionData
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.enumerate.OHQConnectionState

class OmronConnectionListner : RegisterListener{

    /**
     * This function is called when new devices are discovered during scanning.
     * @param discoveredDevices A list of the discovered devices.
     */

    override fun onScanned(discoveredDevices: MutableList<DiscoveredDevice>?) {
        if(discoveredDevices != null){
            // If there are discovered devices, update the device list in the OmronRepository.
            if(discoveredDevices.isNotEmpty()) MainActivity.omronRepo.updateDeviceList(
                discoveredDevices.toList())
        }
    }

    /**
     * This function is called when scanning is completed.
     */
    override fun onScanCompleted() {
        Log.d("TAG", "onScanCompleted: ")
    }

    /**
     * This function is called when registering with the Omron device fails.
     * @param reason The reason for the registration failure.
     */
    override fun onRegisterFailed(reason: OHQCompletionReason?) {
        // Update the Omron connection status in the OmronRepository to false.
        MainActivity.omronRepo.updateOmronConnectionStatus(false)
    }

    /**
     * This function is called when registering with the Omron device is successful.
     */
    override fun onRegisterSuccess() {
        // Update the Omron connection status in the OmronRepository to true.
        MainActivity.omronRepo.updateOmronConnectionStatus(true)
        MainActivity.omronRepo.isDeviceConnected = true

    }

    /**
     * This function is called when the connection state with the Omron device is updated.
     * @param state The updated connection state.
     */
    override fun onConnectionUpdate(state: OHQConnectionState?) {
        Log.d("TAG", "onConnectionUpdate: $state")
    }

    override fun onLastSessionInfoUpdate(sessionData: SessionData) {
        Log.e("TAG", "onLastSessionInfoUpdate: Last synced data $sessionData")
    }

    override fun onSequenceNumberOfLatestRecordUpdate(sequenceNumberOfLatestRecord: Int) {
        MainActivity.omronRepo.setLastSeqNumKey(sequenceNumberOfLatestRecord)
        Log.e("TAG", "onTransferSuccess: last sequenceNumberOfLatestRecord $sequenceNumberOfLatestRecord")
    }

    override fun onDatabaseChangeIncrementUpdate(databaseChangeIncrementKey: Long) {
        MainActivity.omronRepo.setLastIncDecKey(databaseChangeIncrementKey + 1)
        Log.e("TAG", "onTransferSuccess: last databaseChangeIncrementKey $databaseChangeIncrementKey")
    }
}