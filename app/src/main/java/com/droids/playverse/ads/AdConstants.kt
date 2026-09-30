package com.droids.playverse.ads

import android.app.Activity
import android.content.Context
import com.droids.playverse.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdConstants {

    // Resolved per build type from app/build.gradle.kts - debug gets Google's real test IDs, release gets production IDs
    val TEST_BANNER_ID = BuildConfig.BANNER_AD_UNIT_ID
    val TEST_INTERSTITIAL_ID = BuildConfig.INTERSTITIAL_AD_UNIT_ID
    val TEST_REWARDED_ID = BuildConfig.REWARDED_AD_UNIT_ID

}
object InterstitialAdManager {

    private var interstitialAd: InterstitialAd? = null

    fun load(context: Context, adUnitId: String) {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun show(activity: Activity, onDismiss: () -> Unit) {
        if (interstitialAd == null) {
            // ❗ Ad not ready → continue normally
            onDismiss()
            return
        }

        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    onDismiss()
                }
            }

        interstitialAd?.show(activity)
    }

}