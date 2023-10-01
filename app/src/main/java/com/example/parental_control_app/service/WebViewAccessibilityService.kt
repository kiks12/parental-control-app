package com.example.parental_control_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.parental_control_app.data.Site
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.SiteRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WebViewAccessibilityService : AccessibilityService() {

    private var prevApp: String = ""
    private var prevUrl: String = ""
    private val siteRepository = SiteRepository()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {}
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {}
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> resolveWindowContentChangeEvent(event)
            else -> {}
        }
    }

    private fun resolveWindowContentChangeEvent(event: AccessibilityEvent) {
        val parentNodeInfo: AccessibilityNodeInfo = event.source ?: return

        val packageName: String = event.packageName.toString()
        prevApp = packageName

        // Check if the user is using a browser
        // Exit if it is not a supported browser
        val browserConfig: SupportedBrowserConfig = getBrowserConfig(packageName) ?: return

        val capturedUrl: String? = captureUrl(parentNodeInfo, browserConfig)

        Log.w("CAPTURED URL", capturedUrl.toString())

        if (capturedUrl == null || capturedUrl == "") {
            return
        }

        // check if the browser or url has changed since last time
        // to avoid repetitive logs
        if (packageName != prevApp || capturedUrl != prevUrl) {
            if (android.util.Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                if (shouldBeBlocked(capturedUrl)) redirectToBlankPage()
            }
            prevUrl = capturedUrl
        }
    }

    private fun getBrowserConfig(packageName: String): SupportedBrowserConfig? {
        for (supportedConfig in SupportedBrowserConfig.get()) {
            if (supportedConfig.packageName == packageName) {
                return supportedConfig
            }
        }
        return null
    }

    private fun captureUrl(info: AccessibilityNodeInfo, config: SupportedBrowserConfig): String? {
        val nodes: List<AccessibilityNodeInfo> =
            info.findAccessibilityNodeInfosByViewId(config.addressBarId)
        if (nodes.isEmpty()) {
            return null
        }

        val addressBarInfo: AccessibilityNodeInfo = nodes[0]
        return if (addressBarInfo.text != null) {
            addressBarInfo.text.toString()
        } else {
            null
        }
    }

    private fun shouldBeBlocked(capturedUrl: String): Boolean {
        var flag = false
        runBlocking {
            launch {
                for (prohibited in getBlockList()){
                    if (capturedUrl.contains(prohibited.url, ignoreCase = true)) {
                        flag = true
                    }
                }
            }
        }
        return flag
    }

    private suspend fun getBlockList(): List<Site> {
        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val uid = SharedPreferencesManager.getUID(sharedPreferences)

        return siteRepository.getSites(uid!!)
    }

    private fun redirectToBlankPage() {
        Log.d("REDIRECT", "redirecting to blank page")
        val blankPage: Uri = Uri.parse("about:blank")
        val intent = Intent(Intent.ACTION_VIEW, blankPage)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    class SupportedBrowserConfig(val packageName: String, val addressBarId: String) {
        companion object SupportedBrowsers {
            fun get(): List<SupportedBrowserConfig> {
                return listOf(
                    SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"),
                    SupportedBrowserConfig(
                        "org.mozilla.firefox",
                        "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"
                    ),
                    SupportedBrowserConfig("com.opera.browser", "com.opera.browser:id/url_field"),
                    SupportedBrowserConfig(
                        "com.opera.mini.native",
                        "com.opera.mini.native:id/url_field"
                    ),
                    SupportedBrowserConfig(
                        "com.duckduckgo.mobile.android",
                        "com.duckduckgo.mobile.android:id/omnibarTextInput"
                    ),
                    SupportedBrowserConfig("com.microsoft.emmx", "com.microsoft.emmx:id/url_bar"),
                )
            }
        }
    }
}