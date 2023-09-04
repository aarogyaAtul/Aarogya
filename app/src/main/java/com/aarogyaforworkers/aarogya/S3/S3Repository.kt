package com.aarogyaforworkers.aarogya.S3

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.awsauth.S3Manager
import java.io.File

class S3Repository {

    private var isSessionSummaryUploaded : MutableState<Boolean?> = mutableStateOf(null)

    var sessionSummaryUploaded : State<Boolean?> = isSessionSummaryUploaded

    private var imageUrl = ""

    fun updateSessionSummaryUploadStatus(isUploaded : Boolean?, withImageUrl : String){
        isSessionSummaryUploaded.value = isUploaded
        imageUrl = withImageUrl
    }

    fun getUploadedSessionImageURl() : String{
        return imageUrl
    }

    /**
     * Starts uploading the given file to Amazon S3.
     *
     * @param file The file to upload.
     */
    fun startUploadingFile(file : File){
        MainActivity.subUserRepo.updateIsUploading(true)
        S3Manager.shared.setEcgResultCode(MainActivity.pc300Repo.ecgResultCode.value.toString())
        // Call the uploadEcgFile() method of the shared S3Manager object to start uploading the file to Amazon S3
        S3Manager.shared.uploadEcgFile(file)
    }

    fun startUploadingSessionSummary(image: ByteArray, id : String){
        S3Manager.shared.uploadSessionSummary(image, id)
    }


    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: S3Repository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: S3Repository().also { instance = it }
            }
    }


}