package com.droids.playverse.ads

import android.app.Application
import com.droids.playverse.data.ServiceLocator

class AdClass : Application() {

    override fun onCreate() {
        super.onCreate()
        // MobileAds.initialize() is intentionally NOT called here. It's gated behind
        // the UMP consent flow in MainActivity so no ad request fires before consent
        // is resolved — see ConsentManager.
        ServiceLocator.init(this)
    }
}