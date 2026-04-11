package com.example.sms2dingtalk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import java.util.concurrent.Executors

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val prefs = context.getSharedPreferences(MainActivity.PREF_FILE, Context.MODE_PRIVATE)
        val webhook = prefs.getString(MainActivity.PREF_WEBHOOK, "")?.trim().orEmpty()
        if (webhook.isBlank()) return

        val bundle: Bundle = intent.extras ?: return
        val pdus = bundle["pdus"] as? Array<*> ?: return
        val format = bundle.getString("format")

        val senderSb = StringBuilder()
        val bodySb = StringBuilder()

        for (item in pdus) {
            val pdu = item as? ByteArray ?: continue
            val msg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SmsMessage.createFromPdu(pdu, format)
            } else {
                @Suppress("DEPRECATION")
                SmsMessage.createFromPdu(pdu)
            }

            if (msg == null) continue
            if (senderSb.isEmpty()) {
                senderSb.append(msg.originatingAddress ?: "unknown")
            }
            bodySb.append(msg.messageBody)
        }

        val body = bodySb.toString()
        if (body.isBlank()) return

        val secret = prefs.getString(MainActivity.PREF_SECRET, "")
        CodeForwarder.forward(context, "短信", senderSb.toString(), body, webhook, secret)
    }
}
