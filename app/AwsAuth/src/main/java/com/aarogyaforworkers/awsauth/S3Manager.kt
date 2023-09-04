package com.aarogyaforworkers.awsauth

import android.util.Log
import com.amplifyframework.core.Amplify
import java.io.ByteArrayInputStream
import java.io.File

class S3Manager {

    companion object{
        val shared = S3Manager()
    }
    private var sessionEcgFileS3Url : String = ""
    private var isUploaded = false
    private var ecgResult : String = ""
    private var isUploadingStarted = false
    private var ecgFolderPath = "sub_users_ECG_Files/"
    private var imageFolderPath = "sub_users_Profile_Pictures/"
    private var sessionSummaryFolderPath = "sub_users_Session_Summary/"

    fun setEcgResultCode(code : String){
        ecgResult = code
    }

    fun uploadEcgFile(file: File){
        isUploadingStarted = true
        isUploaded = false
        Amplify.Storage.uploadFile("$ecgFolderPath${file.name}_$ecgResult", file,
            {
                sessionEcgFileS3Url = "https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/${it.key}"
                isUploaded = true
                isUploadingStarted = false
                AuthManager.shared.callback?.onEcgFileUploaded(sessionEcgFileS3Url)
                Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
            },
            {
                isUploaded = false
                isUploadingStarted = false
                AuthManager.shared.callback?.onEcgFileUploadedFailed(file)
                Log.e("MyAmplifyApp", "Upload failed", it)
            }
        )
    }

    fun uploadAdminProfilePic(imageArray: ByteArray, adminUUID: String){
        val byteArrayInputStream = ByteArrayInputStream(imageArray)
        Amplify.Storage.uploadInputStream(
            "$imageFolderPath$adminUUID.jpg",
            byteArrayInputStream,
            { result ->
                val url = "https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/${result.key}"
                AuthManager.shared.callback?.onSuccessFullyAdminProfileUploaded(url)
            },
            { error ->
                AuthManager.shared.callback?.onFailedAdminProfileUpload()
            }
        )
    }

    fun uploadSessionSummary(imageArray: ByteArray, sessionUUID : String){
        val byteArrayInputStream = ByteArrayInputStream(imageArray)
        Log.d("TAG", "uploadSessionSummary: start session upload $sessionUUID, $imageArray")
        Amplify.Storage.uploadInputStream(
            "$sessionSummaryFolderPath$sessionUUID.jpg",
            byteArrayInputStream,
            { result ->
                val url = "https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/${result.key}"
                AuthManager.shared.callback?.onSuccessFullySessionSummaryUploaded(url)
                Log.d("TAG", "uploadSessionSummary: Session Uploaded ")
            },
            { error ->
                AuthManager.shared.callback?.onSessionSummaryUploadFailed()
                Log.d("TAG", "uploadSessionSummary: Session failed to upload")
            }
        )
    }

    fun uploadSubUserProfilePicFile(imageArray: ByteArray, subUserUUID: String){
        isUploadingStarted = true
        isUploaded = false
        val byteArrayInputStream = ByteArrayInputStream(imageArray)
        Amplify.Storage.uploadInputStream(
            "$imageFolderPath$subUserUUID.jpg",
            byteArrayInputStream,
            { result ->
                val url = "https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/${result.key}"
                AuthManager.shared.callback?.onSuccessFullySubUserprofileUpdate(url)
            },
            { error ->
                AuthManager.shared.callback?.onSubUserImageUploadFailed()
            }
        )
    }

    fun getSessionUrl() : String{
        if(isUploaded && !isUploadingStarted){
            return sessionEcgFileS3Url
        }else if(!isUploadingStarted){
            return "notStarted"
        }
        return "uploading"
    }

}