package com.aarogyaforworkers.aarogya.PC300

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aarogyaforworkers.aarogya.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive
import com.aarogyaforworkers.aarogya.composeScreens.resetGraphData
import com.creative.SpotCheck.ISpotCheckCallBack
import com.creative.SpotCheck.SpotCheck
import com.creative.SpotCheck.SpotSendCMDThread
import com.creative.base.BaseDate
import com.creative.base.InputStreamReader
import com.creative.base.OutputStreamSender

class PC300Receiver(pC300Repository: PC300Repository, csvRepository: CsvRepository) {

    private var spotCheck : SpotCheck? = null

    private val tag = "PC300Receiver"

    private var lastFrameNo = 0

    /**
     * Initializes a PC-300 receiver by setting up the input and output streams and creating
     * a SpotCheck object that handles the received data. Then starts the PC-300 receiver.
     */
    fun initializeReceiver(){
        // Create an InputStreamReader object with the Bluetooth socket's input stream
        val reader = InputStreamReader(Pc300Manager.shared.bleSocket?.inputStream)
        // Create an OutputStreamSender object with the Bluetooth socket's output stream
        val sender = OutputStreamSender(Pc300Manager.shared.bleSocket?.outputStream)
        // Create a SpotCheck object with the InputStreamReader, OutputStreamSender, and callback function
        spotCheck = SpotCheck(reader, sender, spotCheckCallBack)
        // Start the PC-300 receiver by calling the SpotCheck object's Start() function
        startPC300Receiver()
    }

    /**
     * Starts the PC-300 receiver by calling the SpotCheck object's Start() function.
     *
     * @return The SpotCheck object's Start() function's return value.
     */
    private fun startPC300Receiver() = spotCheck?.Start()

    @OptIn(ExperimentalStdlibApi::class)
    private val spotCheckCallBack : ISpotCheckCallBack = object : ISpotCheckCallBack {

        /**
         * This function is called when the connection to a PC300 device is lost.
         * It updates the PC300 connection status to false and the PC300 disconnection status to true.
         * It also logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnConnectLose() {
            Log.d(tag, "OnConnectLose: ")
            pC300Repository.updatePC300ConnectionStatus(false)
            pC300Repository.updatePc300DisconnectionStatus(true)
        }

        /**
         * This function is called when the device name is retrieved.
         * It logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnGetDeviceName(p0: String?) {
            Log.d(tag, "OnGetDeviceName: ")
        }

        /**
         * This function is called when the device version is retrieved.
         * It logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnGetDeviceVer(p0: String?, p1: String?, p2: Int, p3: Int) {
            Log.d(tag, "OnGetDeviceVer: ")
        }

        /**
         * This function is called when the NIBP (non-invasive blood pressure) mode is retrieved.
         * It logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnGetNIBPMode(p0: Int) {
            Log.d("TAG", "OnGetNIBPMode: Mode $p0")
            Log.d(tag, "OnGetNIBPMode: ")
        }

        /**
         * This function is called when the ECG (electrocardiogram) version is retrieved.
         * It logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnGetECGVer(p0: String?, p1: String?) {
            Log.d(tag, "OnGetECGVer: ")
        }

        /**
         * This function is called when the SpO2 (blood oxygen saturation) action is retrieved.
         * It logs the event using the Android Log class with the tag specified for this class.
         */
        override fun OnGetSpO2Action(p0: Int) {
            Log.d(tag, "OnGetSpO2Action: ")
        }

        /**
         * This function is called when the SpO2 parameters are retrieved.
         * It updates the SpO2 and HR values in the repository using the values provided.
         * It uses the Android Log class to log the event with the tag specified for this class.
         */
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetSpO2Param(p0: Int, p1: Int, p2: Float, p3: Boolean, p4: Int) {
            // Update SpO2 and HR values in the repository
            pC300Repository.updateSpO2("$p0 %")
            pC300Repository.updateHR("$p1 bpm")
        }

        override fun OnGetSpO2Wave(waveData: MutableList<BaseDate.Wave>?) {
            Log.d(tag, "OnGetSpO2Wave: ")
            if(waveData == null) return
            StaticReceive.DRAWDATA.addAll(waveData)
            StaticReceive.DRAWDATA.addAll(waveData)
            StaticReceive.SPOWAVE.addAll(waveData)
            StaticReceive.isECGData = false

        }

        override fun OnGetNIBPRealTime(p0: Boolean, p1: Int) {
            Log.d(tag, "OnGetDataMode BP realtime: $p0, $p1")
            // Update blood pressure value in the repository with the received value
            if(p1 != 0){
                pC300Repository.checkBpTimeOut()
                pC300Repository.updateBloodPressure("$p1")
            }
        }

        // Update HR, systolic, diastolic, and blood pressure values in the repository with the received values
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetNIBPResult(
            p0: Boolean,
            p1: Int,
            p2: Int,
            p3: Int,
            p4: Int,
            p5: Int,
            p6: Int
        ) {
            Log.d("TAG", "OnGetDataMode: BP $p0, $p1, $p2, $p3, $p4, $p5, $p6")
            pC300Repository.stopBpTimer()
            pC300Repository.updateHR("$p1 bpm")
            pC300Repository.updateSys("$p3")
            pC300Repository.updateDia("$p4")
            pC300Repository.updateBloodPressure("$p3/$p4 mmHg")
        }

        override fun NIBP_StartStaticAdjusting() {
            Log.d("TAG", "OnGetDataMode: BP staticadjusting")

            Log.d(tag, "NIBP_StartStaticAdjusting: ")
        }

        /**
         * update ecg status and ecg data
         * @waveData - ecg data point
         * status -
         * 0 = started
         * 1 = measuring
         * 2 = done measuring
         * */
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetECGRealTime(waveData: BaseDate.ECGData?, nHR: Int, p2: Boolean, p3: Int) {
            Log.d(tag, "OnGetECGRealTime: ${waveData?.data}")
            Log.d("TAG", "OnGetDataMode: ECG ${waveData?.data}")

            // Update ECG result status value in the repository with 1
            pC300Repository.updateEcgResult(1)
            if(waveData!!.frameNum == 1) {
                resetGraphData()
                StaticReceive.DRAWDATA.clear()
                lastFrameNo = 0
            }
            pC300Repository.checkEcgTimeOut()

            //System.out.println("OnGetECGRealTime nHR:"+nHR);
            StaticReceive.isECGData = true
            StaticReceive.is128 = p3 == 255
            //System.out.println("OnGetECGRealTime nHR:"+nHR);
            StaticReceive.DRAWDATA.addAll(waveData.data)
            val data = StaticReceive.DRAWDATA.removeAt(0)
            Log.d("TAG", "RealtimeGraph: adding new data to drwaData ${StaticReceive.DRAWDATA} value")
            Pc300Manager.shared.addECgData()
            // update data in CSV File
            if (lastFrameNo + 1 == waveData.frameNum) {
                lastFrameNo = waveData.frameNum
//                csvRepository.writeDataToFile(waveData)
            }
        }

        override fun onGetECGGain(p0: Int, p1: Int) {
            Log.d(tag, "onGetECGGain: ")
            gain = if (gain == 0) 2 else gain
            Log.d("TAG", "OnGetDataMode: ECG Gain ${p0} $p1")

        }

        // update ecg result
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetECGResult(p0: Int, p1: Int) {
            pC300Repository.updateEcgResult(2)
            StaticReceive.DRAWDATA.clear()
            MainActivity.pc300Repo.cleanDisplayBuffer()
            if(p0 != 127 && p1 != 127) pC300Repository.stopEcgTimer()
            pC300Repository.updateHR("$p1 bpm")
            when(p0){
                255 -> {
                    pC300Repository.updateEcgResult(3)
                }
                else -> {
                    MainActivity.pc300Repo.updateEcgResultCode(p0)
                }
            }

        }

        // update temp value from pc300 to UI
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetTmp(p0: Boolean, p1: Boolean, p2: Float, p3: Int, p4: Int) {
            Log.d("TAG", "OnGetTmp: $p2")
            pC300Repository.updateTempValue("$p2 °C")
        }

        override fun onGetTMP_Mode(p0: Int, p1: Int) {
            Log.d(tag, "onGetTMP_Mode: ")
        }

        override fun OnGetGlu(p0: Float, p1: Int, p2: Int) {
            Log.d(tag, "OnGetGlu: ")
            MainActivity.pc300Repo.updateGluValue(p0.toString())
        }

        override fun OnGetUA(p0: Float, p1: Int) {
            Log.d(tag, "OnGetUA: ")
        }

        override fun OnGetCHOL(p0: Float, p1: Int) {
            Log.d(tag, "OnGetCHOL: ")
        }

        override fun OnGetNIBPStatus(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.d(tag, "OnGetNIBPStatus: ")
        }

        override fun OnGetSpO2Status(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.d(tag, "OnGetSpO2Status: ")
        }

        override fun OnGetGluStatus(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.d(tag, "OnGetGluStatus: ")
        }

        override fun OnGetTmpStatus(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.d(tag, "OnGetTmpStatus: ")
        }

        override fun OnGetNIBPAction(p0: Int) {
            Log.d(tag, "OnGetNIBPAction: ")
        }

        override fun OnGetGLUAction(p0: Int) {
            Log.d(tag, "OnGetGLUAction: ")
        }

        override fun OnGetGLU_DeviceType(p0: Int) {
            Log.d(tag, "OnGetGLU_DeviceType: ")
        }

        override fun OnSetGLU_DeviviceType(p0: Int) {
            Log.d(tag, "OnSetGLU_DeviviceType: ")
        }

        override fun OnGetTMPAction(p0: Int) {
            Log.d(tag, "OnGetTMPAction: ")
        }

        var sendCnt = 0

        // update ecg start, stop status
        @RequiresApi(Build.VERSION_CODES.O)
        override fun OnGetECGAction(status: Int) {
            // must set ECG Bit in standby mode.
            // don't set, default is 8bit ECG
            // only send one commond is ok,
            //0 - standby
            //1 - being to measure
            //2 - stop
            Log.d("TAG", "OnGetECGAction: ecgStatus $status")

            if(status == 1){
                pC300Repository.createNewECGFile(MainActivity.csvRepository, MainActivity.subUserRepo.getSessionId())
                pC300Repository.updateEcgResult(0)
                pC300Repository.checkEcgTimeOut()
                MainActivity.pc300Repo.cleanDisplayBuffer()
            }

            if(status == 2){
                pC300Repository.updateEcgResult(4)
                pC300Repository.stopEcgTimer()
            }
            if (status == 0 && sendCnt < 1) {
                //set 12 bit ECG
                SpotSendCMDThread.Send12BitECG()
                println("send 12 bit cmd")
                sendCnt++
            } else if (status == 1) {
                sendCnt = 0
            }
            Log.d(tag, "OnGetECGAction: ")
        }

        override fun OnGetPowerOff() {
            Log.d(tag, "OnGetPowerOff: ")
        }

        override fun onIAP_version(p0: Int, p1: Int, p2: Byte) {
            Log.d(tag, "onIAP_version: ")
        }

        override fun onCustomUserId(p0: String?) {
            Log.d(tag, "onCustomUserId: ")
        }
    }

    companion object{
        val shared = PC300Receiver(MainActivity.pc300Repo, MainActivity.csvRepository)
    }

}