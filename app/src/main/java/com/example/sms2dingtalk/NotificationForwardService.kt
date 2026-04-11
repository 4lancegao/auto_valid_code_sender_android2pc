package com.example.sms2dingtalk

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationForwardService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName == packageName) return

        val extras = sbn.notification?.extras ?: return
        val parts = buildList {
            addIfNotBlank(extras.getCharSequence(Notification.EXTRA_TITLE)?.toString())
            addIfNotBlank(extras.getCharSequence(Notification.EXTRA_TEXT)?.toString())
            addIfNotBlank(extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString())
            addIfNotBlank(extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString())
            addIfNotBlank(extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString())
        }.filterNotNull()

        if (parts.isEmpty()) return
        val source = "通知 [${sbn.packageName}]"
        val sender = parts[0]
        val content = parts.joinToString(separator = "\n")

        val prefs = getSharedPreferences(MainActivity.PREF_FILE, MODE_PRIVATE)
        val webhook = prefs.getString(MainActivity.PREF_WEBHOOK, "")?.trim().orEmpty()
        if (webhook.isBlank()) return
        val secret = prefs.getString(MainActivity.PREF_SECRET, "")

        CodeForwarder.forward(this, source, sender, content, webhook, secret)
    }

    private fun MutableList<String>.addIfNotBlank(value: String?) {
        if (!value.isNullOrBlank()) {
            add(value.trim())
        }
    }
}
