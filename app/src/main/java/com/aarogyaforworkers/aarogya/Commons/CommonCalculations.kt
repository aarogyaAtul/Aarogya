package com.aarogyaforworkers.aarogya.composeScreens

import java.text.SimpleDateFormat
import java.util.Locale

fun getRange(gender : String) : String{
    return when(gender){
        "Male", "male", "MALE" -> {
            "6.0 - 24.0"
        }
        "Female", "female", "FEMALE"  -> {
            "21.0 - 32.0"
        }
        else -> {
            "0.0-0.0"
        }
    }
}

fun getValidRange(gender : String) : ClosedRange<Double>{
    return when(gender){
        "Male", "male", "MALE" -> {
            6.0..24.0
        }
        "Female", "female", "FEMALE"  -> {
            21.0..32.0
        }
        else -> {
            0.0..0.0
        }
    }
}

fun checkIsMale(gender : String) : Boolean{
    return when(gender){

        "Male", "male", "MALE" -> {
            true
        }

        "Female", "female", "FEMALE"  -> {
            false
        }

        else -> {
            true
        }
    }
}

fun formatTitle(firstName: String, lastName: String): String {
    // Convert first character of first name to uppercase
    var firstName = firstName
    var lastName = lastName
    if(firstName.isNotEmpty()){
        val firstChar = firstName[0].uppercaseChar()
        firstName = firstChar.toString() + firstName.substring(1)
    }
    if(lastName.isNotEmpty()){
        // Convert first character of last name to uppercase
        val firstChar = lastName[0].uppercaseChar()
        lastName = firstChar.toString() + lastName.substring(1)
    }
    // Create the formatted title string
    return "$firstName $lastName"
}



fun convertCustomDateFormat(date: String) : String{
    if(date.isEmpty()) return ""
    var monthArray : List<String> = listOf<String>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val d = date.split("/")
    if(d.size == 3){
        val day = d[0]
        val mon = monthArray[d[1].toInt() - 1]
        val year = d[2]
        val date = day + " " + mon + " "+ year
        return date
    }else {
        return ""
    }
}

fun convertDateFormat(date: String): String {
    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val inputDate = inputFormat.parse(date)
    val convertedDate = outputFormat.format(inputDate)
    return convertedDate
}

fun convertTimeToAMPMFormat(time: String): String {
    var inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    if(time.length < 6){
        inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }
    val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    try {
        if (time.isEmpty()) return ""
        val inputTime = inputFormat.parse(time)
        val convertedTime = outputFormat.format(inputTime)
        return convertedTime.toUpperCase(Locale.getDefault())
    } catch (e: Exception) {
        // Handle any exceptions that occur
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        e.printStackTrace()
        return ""
    }
}

fun getSessionLocation(location : String) : String{
    var loc = location.split("/")
    return if(loc.size == 6){
        val city = loc[1]
        city
    }else{
        loc = location.split(",")
        val city = loc[0]
        city
    }
}