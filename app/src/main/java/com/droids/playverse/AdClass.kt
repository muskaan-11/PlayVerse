package com.droids.playverse

import android.app.Application
import com.google.android.gms.ads.MobileAds

class AdClass : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
