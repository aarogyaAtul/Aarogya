package com.aarogyaforworkers.aarogya.CsvGenerator

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aarogyaforworkers.aarogya.Camera.CameraRepository
import com.aarogyaforworkers.aarogya.PC300.Pc300Manager
import com.creative.base.BaseDate.ECGData
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalTime

class CsvRepository {

    private var FILENAME = "ECG_output.csv"


    fun setUpContext(context: Activity){
        contexts = context
    }

    fun setUpNewContext(conte: Context){
        context = conte
    }

    fun getContext() : Activity?{
        Log.e("TAG", "writeDataToFile: is context $contexts")
        return contexts
    }
    /**
     * Checks if the ECG directory exists and creates it if it doesn't.
     * @param context The activity context.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun checkECGDirectory(context: Activity) {
        setUpContext(context)
        val directory = context.getExternalFilesDir("ECGFiles")
        if (!directory!!.exists()) {
            directory.mkdirs()
        }
    }

    /**
     * Updates the name of the ECG file with the provided session ID and creates a new file with that name.
     * If a file with that name already exists, it is deleted before a new file with that name is created.
     * @param sessionId The ID of the current session.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun updateFileName(sessionId : String) {
        // Get the directory to store the file
        if(context == null) {
            return
        }
        val directory = context!!.getExternalFilesDir("ECGFiles")
        // Create a new file in the directory with the specified filename
        val time = LocalTime.now().toString()
        FILENAME = sessionId+"_.csv"
        val file = File(directory, FILENAME)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }else{
                file.delete()
                file.createNewFile()
            }
        } catch (e: IOException) {
            Log.e("TAG", "writeDataToFile: is error")
            e.printStackTrace()
        }
    }


    fun writeDataToFile(waveDataPoint: String) {
        if (context == null) {
            return
        }
        // Get the directory to store the file
        val directory = context!!.getExternalFilesDir("ECGFiles")
        // Create a new file in the directory with the specified filename
        val file = File(directory, FILENAME)
        // Check if the file already exists
        if (file.exists()) {
            try {
                // Open a FileWriter in append mode to write data to the end of the file
                val writer = FileWriter(file, true) // true to append data to the end of file
                val dataframe = "$waveDataPoint\n" // Append a newline character to separate data points
                writer.append(dataframe) // Write the data to the end of the file
                writer.flush() // Flush the writer to ensure all data is written to the file
                writer.close() // Close the writer to release the file resources
                Pc300Manager.shared.clearECgData()
                Log.e("TAG", "writeDataToFile: is saving")
            } catch (e: IOException) {
                Log.e("TAG", "writeDataToFile: is not saving")
                e.printStackTrace() // Print any IOException that occurs
            }
        }
    }

//    fun writeDataToFile(waveDataPoint: String) {
//        if(context == null) {
//            return
//        }
//        // Get the directory to store the file
//        val directory = context!!.getExternalFilesDir("ECGFiles")
//        // Create a new file in the directory with the specified filename
//        val file = File(directory, FILENAME)
//        // Check if the file already exists
//        if (file.exists()) {
//            try {
//                // Open a FileWriter in append mode to write data to the end of the file
//                val writer = FileWriter(file, true) // true to append data to the end of file
//                val dataframe = """
//                ${waveDataPoint}
//                """.trimIndent()
//                writer.append(dataframe)  // Write the data to the end of the file
//                writer.flush() // Flush the writer to ensure all data is written to the file
//                writer.close() // Close the writer to release the file resources
//                Log.e("TAG", "writeDataToFile: is saving")
//            } catch (e: IOException) {
//                Log.e("TAG", "writeDataToFile: is not saving")
//                e.printStackTrace() // Print any IOException that occurs
//            }
//        }
//    }


    fun writeDataToFile(waveData: ECGData) {
        if(context == null) {
            return
        }
        // Get the directory to store the file
        val directory = context!!.getExternalFilesDir("ECGFiles")
        // Create a new file in the directory with the specified filename
        val file = File(directory, FILENAME)
        // Check if the file already exists
        if (file.exists()) {
            try {
                // Open a FileWriter in append mode to write data to the end of the file
                val writer = FileWriter(file, true) // true to append data to the end of file
                val dataframe = """
                ${waveData.data[0].data}
                ${waveData.data[1].data}
                ${waveData.data[2].data}
                ${waveData.data[3].data}
                ${waveData.data[4].data}
                ${waveData.data[5].data}
                ${waveData.data[6].data}
                ${waveData.data[7].data}
                ${waveData.data[8].data}
                ${waveData.data[9].data}
                ${waveData.data[10].data}
                ${waveData.data[11].data}
                ${waveData.data[12].data}
                ${waveData.data[13].data}
                ${waveData.data[14].data}
                ${waveData.data[15].data}
                ${waveData.data[16].data}
                ${waveData.data[17].data}
                ${waveData.data[18].data}
                ${waveData.data[19].data}
                ${waveData.data[20].data}
                ${waveData.data[21].data}
                ${waveData.data[22].data}
                ${waveData.data[23].data}
                ${waveData.data[24].data}
               
                """.trimIndent()
                writer.append(dataframe)  // Write the data to the end of the file
                writer.flush() // Flush the writer to ensure all data is written to the file
                writer.close() // Close the writer to release the file resources
                Log.e("TAG", "writeDataToFile: is saving")
            } catch (e: IOException) {
                Log.e("TAG", "writeDataToFile: is not saving")
                e.printStackTrace() // Print any IOException that occurs
            }
        }
    }

    fun getLastFile() : File?{
        return if(context != null){
            // Get the directory where the file is stored
            val directory = context!!.getExternalFilesDir("ECGFiles")
            if(directory != null) {
                val totalFiles = directory.listFiles()
                totalFiles.last()
            } else null
        }else{
            null
        }
    }




    fun getSessionFile() : File?{
        if(context != null){
            // Get the directory where the file is stored
            val directory = context!!.getExternalFilesDir("ECGFiles")
            // Create a new file object with the specified filename
            val file = File(directory, FILENAME)
            // If the file exists, return it, otherwise return null
            if(file.exists()) return file else return null
        }else{
            return null
        }
    }


    companion object {

        @Volatile private var contexts: Activity? = null

        @Volatile private var context: Context? = null
        // Singleton instantiation you already know and love
        @Volatile private var instance: CsvRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CsvRepository().also { instance = it }
            }
    }
}