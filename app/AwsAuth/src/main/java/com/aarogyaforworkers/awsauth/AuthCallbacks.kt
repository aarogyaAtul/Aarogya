package com.aarogyaforworkers.awsauth

import java.io.File

interface AuthCallbacks {

    fun onSignInSuccess()

    fun onSignInOTPSent()

    fun onSignInOTPFailed()

    fun onAdminUserIdUpdate(id: String)

    fun onSignOutSuccess()

    fun onSignInFailed(reason : String)

    fun onForgotPasswordConfirmationOTPSent()

    fun onForgorPasswordUserNotFound()

    fun onForgotPasswordConfirmationOTPFailed(reason: String)

    fun onSuccessFullPasswordReset()

    fun onPasswordResetFailure(reason: String)

    fun onEcgFileUploaded(withLink : String)

    fun onEcgFileUploadedFailed(withFile : File)

    fun onSuccessFullySubUserprofileUpdate(withImageUrl : String)

    fun onSuccessFullyAdminProfileUploaded(withImageUrl : String)

    fun onSuccessFullySessionSummaryUploaded(withImageUrl: String)

    fun onSessionSummaryUploadFailed()

    fun onFailedAdminProfileUpload()

    fun onSubUserImageUploadFailed()

    fun onInvalidPassword()

    fun onInvalidOTP()

    fun onInvalidUserName()

    fun onUserAllReadySignedIn()

    fun onWrongPasswordResetOTP()

}