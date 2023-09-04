package com.aarogyaforworkers.aarogya

class Common {

    val login = LoginFlowTest.getInstance()

    companion object{
        val shared = Common()
    }
}