package com.droids.playverse.ads

import android.app.Activity
import android.content.Context
import com.droids.playverse.data.ServiceLocator
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

object RewardedAdManager {

    private var rewardedAd: RewardedAd? = null
    private val rewardCredited = AtomicBoolean(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun load(context: Context, adUnitId: String) {
        val request = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    fun show(
        activity: Activity,
        adUnitId: String,
        source: String,
        rewardAmount: Int? = null,
        onRewardEarned: (Int) -> Unit,
        onDismiss: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            onDismiss()
            return
        }

        rewardCredited.set(false)

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                load(activity, adUnitId)
                onDismiss()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                load(activity, adUnitId)
                onDismiss()
            }
        }

        ad.show(activity) { rewardItem ->
            if (rewardCredited.compareAndSet(false, true)) {
                val amount = rewardAmount ?: rewardItem.amount
                scope.launch {
                    ServiceLocator.provideCoinRepository().earn(amount, source)
                }
                onRewardEarned(amount)
            }
        }
    }
}