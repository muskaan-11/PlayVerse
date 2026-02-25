package com.droids.playverse

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdConstants {

    // ✅ Google Test Ad Unit IDs (SAFE for development)---
    // IMPORTANT
    // this is the ad unit only real one not changing name as i need to chnage in all places then
    const val TEST_BANNER_ID = "ca-app-pub-8678839926764431/5038534528"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-8678839926764431/2686977000"

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
