package com.droids.playverse.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Wraps the Google User Messaging Platform (UMP) SDK so the rest of the app
 * doesn't need to know about ConsentInformation / ConsentForm plumbing.
 *
 * Usage: call [requestConsent] once, as early as possible (e.g. MainActivity.onCreate,
 * before setContent). It resolves immediately for users outside regions that require
 * consent, and shows a form first for EU/EEA/UK users. Only call MobileAds.initialize()
 * and load your first ad from inside the completion callback (or after checking
 * [canRequestAds]), so no ad request goes out before consent is resolved.
 */
object ConsentManager {

    /**
     * Requests the latest consent status from Google and, if required, shows the
     * consent form. [onConsentResolved] is invoked exactly once, whether or not a
     * form was actually shown, and whether or not the update succeeded — callers
     * should treat it as "the consent flow is done, now check canRequestAds()".
     */
    fun requestConsent(activity: Activity, onConsentResolved: () -> Unit) {
        val params = ConsentRequestParameters.Builder().build()
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    // formError is ignored deliberately: whether the form loaded,
                    // was shown, or wasn't required, the flow is now resolved.
                    onConsentResolved()
                }
            },
            {
                // Consent info couldn't be fetched (e.g. no network). Don't block
                // the app indefinitely — resolve so canRequestAds() can be checked;
                // it will simply report false until a future launch succeeds.
                onConsentResolved()
            }
        )
    }

    /** Whether we're currently allowed to request ads under the resolved consent state. */
    fun canRequestAds(context: Context): Boolean {
        return UserMessagingPlatform.getConsentInformation(context).canRequestAds()
    }

    /**
     * Whether the privacy options entry point (a "Privacy Options" row that reopens
     * the consent form) should be shown, e.g. from the Settings screen.
     */
    fun isPrivacyOptionsRequired(context: Context): Boolean {
        return UserMessagingPlatform.getConsentInformation(context).privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
}