package com.example.sms2dingtalk

import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object DingTalkSender {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun send(webhook: String, secret: String?, content: String) {
        if (webhook.isBlank()) return

        val url = if (!secret.isNullOrBlank()) {
            val ts = System.currentTimeMillis()
            val sign = buildSignedQuery(secret, ts)
            val sep = if (webhook.contains("?")) "&" else "?"
            "$webhook${sep}timestamp=$ts&sign=$sign"
        } else {
            webhook
        }

        val payload = JSONObject().apply {
            put("msgtype", "text")
            put("text", JSONObject().put("content", content))
        }.toString()

        val request = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(jsonType))
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().close()
    }

    private fun buildSignedQuery(secret: String, ts: Long): String {
        val strToSign = "$ts\n$secret"
        val mac = Mac.getInstance("HmacSHA256")
        val secretSpec = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(secretSpec)
        val bytes = mac.doFinal(strToSign.toByteArray(StandardCharsets.UTF_8))
        val sign = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return URLEncoder.encode(sign, "UTF-8")
    }
}
