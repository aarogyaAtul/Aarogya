package com.aarogyaforworkers.awsapi.models

data class AdminProfile(val admin_id : String, val email : String, val phone : String, val first_name : String, val last_name : String, val age : String, val gender : String, val weight : String, val height : String, val location : String, var profile_pic_url : String, val total_sessions_taken : String, val total_users_added : String)