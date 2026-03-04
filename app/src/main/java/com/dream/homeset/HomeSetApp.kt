package com.dream.homeset

import android.app.Application
import com.dream.homeset.core.network.NetworkModule

class HomeSetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this)
    }
}

