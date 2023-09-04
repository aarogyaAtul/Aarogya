package com.aarogyaforworkers.aarogya.Omron

import android.util.Log
import com.aarogyaforworkers.aarogya.MainActivity
import net.huray.omronsdk.OmronDeviceManager.TransferListener
import net.huray.omronsdk.ble.entity.SessionData
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.enumerate.OHQMeasurementRecordKey
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey
import java.math.BigDecimal

class OmronTransferListner : TransferListener {

    // method for handling failed Omron transfers
    override fun onTransferFailed(reason: OHQCompletionReason?) {
        Log.d("TAG", "onTransferSuccess: process failed retry")
        if(MainActivity.omronRepo.isAllReadyExecuting) MainActivity.omronRepo.isAllReadyExecuting = false
        when(MainActivity.omronRepo.isReadyForFetch){
            true -> MainActivity.omronRepo.fetchUserProfile()
            false -> MainActivity.omronRepo.setUserProfile()
        }
    }

    // Override method for handling successful Omron transfers
    override fun onTransferSuccess(sessionData: SessionData?) {
        if(MainActivity.omronRepo.isAllReadyExecuting) MainActivity.omronRepo.isAllReadyExecuting = false
        if(sessionData != null){
            when{
                MainActivity.omronRepo.getSelectedPatient()?.get(OHQUserDataKey.HeightKey) != null && sessionData.userData?.get(OHQUserDataKey.HeightKey) != null -> {
                    val height = sessionData.userData?.get(OHQUserDataKey.HeightKey) as BigDecimal
                    val selectedPatientHeight = MainActivity.omronRepo.getSelectedPatient()?.get(OHQUserDataKey.HeightKey) as BigDecimal
                    when{
                        MainActivity.omronRepo.getSelectedPatient()?.get(OHQUserDataKey.DateOfBirthKey) == sessionData.userData?.get(OHQUserDataKey.DateOfBirthKey) &&
                                height.toInt() == selectedPatientHeight.toInt() && MainActivity.omronRepo.getSelectedPatient()?.get(OHQUserDataKey.GenderKey) == sessionData.userData?.get(OHQUserDataKey.GenderKey) -> {
                            // user is synced and data is ready to collect
                            if(!sessionData.measurementRecords.isNullOrEmpty()){
                                val results = sessionData.measurementRecords?.last()
                                if(results != null){
                                    val weight = results[OHQMeasurementRecordKey.WeightKey].toString()
                                    val weightInt = String.format("%.1f", weight.toDouble())
                                    val bodyFat = results[OHQMeasurementRecordKey.BodyFatPercentageKey].toString()
                                    val fatInPercent = String.format("%.1f", bodyFat.toDouble() * 100)
                                    val bmi = results[OHQMeasurementRecordKey.BMIKey].toString()
                                    val bmiInt = String.format("%.1f", bmi.toDouble())
                                    val age = results[OHQMeasurementRecordKey.BodyAgeKey].toString()
                                    val height = results[OHQMeasurementRecordKey.HeightKey].toString()
                                    val newInfo = UserWeightInfo(weightInt, fatInPercent, bmiInt, age, height)
                                    MainActivity.omronRepo.updateLatestUserWeight(newInfo)
                                    MainActivity.omronRepo.updateDeviceStatus("")
                                }
                            }else{
                                MainActivity.omronRepo.updateDeviceStatus("")
                            }
                            MainActivity.omronRepo.isReadyForFetch = true
                            MainActivity.omronRepo.fetchUserProfile()
                            Log.e("TAG", "onTransferSuccess: is Synced ${sessionData.userData}")
                                }
                        else -> { // user is not synced
                            MainActivity.omronRepo.isReadyForFetch = false
                            MainActivity.omronRepo.setUserProfile()
                            MainActivity.omronRepo.updateDeviceStatus("Syncing")
                            Log.e("TAG", "onTransferSuccess: is Not Synced ${sessionData.userData}")
                        }
                    }
                }
            }
        }else{
            MainActivity.omronRepo.setUserProfile()
        }
    }

    override fun onLastSessionInfoUpdate(sessionData: SessionData) {
        Log.e("TAG", "onTransferSuccess: tt Last synced data $sessionData")
    }

    override fun onSequenceNumberOfLatestRecordUpdate(sequenceNumberOfLatestRecord: Int) {
        MainActivity.omronRepo.setLastSeqNumKey(sequenceNumberOfLatestRecord)
        Log.e("TAG", "onTransferSuccess: tt last sequenceNumberOfLatestRecord $sequenceNumberOfLatestRecord")
    }

    override fun onDatabaseChangeIncrementUpdate(databaseChangeIncrementKey: Long) {
        MainActivity.omronRepo.setLastIncDecKey(databaseChangeIncrementKey)
        Log.e("TAG", "onTransferSuccess: tt last databaseChangeIncrementKey $databaseChangeIncrementKey")
    }
}