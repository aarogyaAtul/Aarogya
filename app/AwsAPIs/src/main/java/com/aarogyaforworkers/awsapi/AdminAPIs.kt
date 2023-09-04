package com.aarogyaforworkers.awsapi

import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AdminAPIs {

    @POST("admins_profile")
    fun createNewAdmin(@Body resource: AdminProfile): Call<ResponseBody>

    @PUT("admins_profile")
    fun updateAdminProfilePic(@Body resource: AdminProfile): Call<ResponseBody>

    @GET("admins_profile/byphone")
    fun getAdminProfileByPhone(@Query("phone") phone: String) : Call<ResponseBody>

    @POST("sub_users_profile")
    fun createNewSubUser(@Body resource: SubUserProfile): Call<ResponseBody>

    @GET("sub_users_profile")
    fun getSubUsersProfileById(@Query("user_id") query: String): Call<ResponseBody>

    @GET("sub_users_profile/get_by_phone")
    fun getSubUsersProfileByPhone(@Query("phone") query: String): Call<ResponseBody>

    @PUT("sub_users_profile")
    fun updateSubUser(@Body resource: SubUserProfile): Call<ResponseBody>

    @GET("verify_sub_user_phone")
    fun sendSubUserVerificationCode(@Query("phone") phone: String) : Call<ResponseBody>

    @GET("admins_profile")
    fun getAdminsProfile(@Query("admin_id") adminId: String): Call<ResponseBody>

    @GET("search_sub_users")
    fun searchSubUsersProfile(@Query("query") query: String): Call<ResponseBody>

    @POST("sub_users_sessions")
    fun createNewSession(@Body resource: Session) : Call<ResponseBody>

    @DELETE("sub_users_sessions/session")
    fun deleteSession(@Query("sessionId") query: String) : Call<ResponseBody>

    @DELETE("sub_users_sessions")
    fun deleteSessionForUser(@Query("user_id") query: String) : Call<ResponseBody>

    @PUT("sub_users_sessions")
    fun updateSessionBySessionId(@Body resource: Session) : Call<ResponseBody>

    @PUT("sub_users_sessions/session")
    fun updateFullSessionBySessionId(@Body resource: Session) : Call<ResponseBody>

    @GET("sub_users_sessions")
    fun getSessionByUserID(@Query("userId") userId: String) : Call<ResponseBody>

}