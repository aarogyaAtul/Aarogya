package com.aarogyaforworkers.aarogya.Omron

import android.app.Application

class App : Application(){

    init {
        application = this
    }

    companion object {
        private var application: App? = null

        val instance: App? get() = application
    }
}