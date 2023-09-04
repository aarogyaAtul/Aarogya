package com.aarogyaforworkers.aarogya.Camera

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.aarogyaforworkers.aarogya.Auth.AuthRepository

class CameraRepository {

    private var isImageCaptured : MutableState<Bitmap?> = mutableStateOf(null)

    private var isImageCapturedFailed : MutableState<Boolean?> = mutableStateOf(false)

    var capturedImageBitmap : State<Bitmap?> = isImageCaptured

    var capturedImageFailedState : State<Boolean?> = isImageCapturedFailed

    fun updateCapturedImage(bitmap: Bitmap){
        isImageCaptured.value = bitmap
    }

    fun onImageClickFailed(isFailed : Boolean){
        isImageCapturedFailed.value = isFailed
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: CameraRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CameraRepository().also { instance = it }
            }
    }
}