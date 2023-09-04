package com.aarogyaforworkers.awsapi

import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile

interface APICallbacks {

    fun onSuccessAdminProfileResult(profile: MutableList<AdminProfile>)

    fun onSuccessAdminProfilePicUpdated(newPicURL : String)

    fun onAdminProfilePicUpdateFailed()

    fun onSubUserProfileFound(profile: MutableList<SubUserProfile>)

    fun onSubUserProfileNotFound()

    fun onGuestSessionsDeleted()

    fun onGuestSessionDeleteFailed()

    fun userAllReadyRegistered()

    fun userIsNotRegistered()

    fun onSearchSubUserProfileResult(profile: MutableList<SubUserProfile>)

    fun onSuccessSubUserSessions(sessions: MutableList<Session>)

    fun onNoEmailFoundByPhone(error : String)

    fun onSuccessfullyEmailFoundByPhone(email : String)

    fun onSuccessfullyEmailFoundByPhone(email : String, password : String)

    fun onFailedSubUserSessions()

    fun onSingleSessionDeleted()

    fun onSingleSessionUpdated()

    fun onSessionDeleteFailed()

    fun onSuccessRemarkUpdate()

    fun onFailedRemarkUpdate()

    fun onSuccessPostSession()

    fun onFailedPostSession()

    fun onCreateUpdateSubUserProfileResult(isSuccess: Boolean)

    fun onFailedAdminProfileResult(withError : String)

    fun onFailedSearchProfileResult()

    fun onSuccessSubUserVerificationCodeSent(verificationCode : String)

    fun onVerificationCodeFailed()

}