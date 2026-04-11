package com.example.sms2dingtalk

import android.content.Context
import java.util.concurrent.Executors

object CodeForwarder {
    private val executor = Executors.newSingleThreadExecutor()

    fun forward(
        context: Context,
        sourceType: String,
        sender: String,
        rawContent: String,
        webhook: String,
        secret: String?
    ) {
        if (webhook.isBlank()) return

        val code = CodeExtractor.extractCode(rawContent) ?: return
        val message = CodeExtractor.buildMessage(sourceType, sender, code, rawContent)

        executor.execute {
            try {
                DingTalkSender.send(webhook, secret, message)
            } catch (_: Exception) {
            }
        }
    }
}
