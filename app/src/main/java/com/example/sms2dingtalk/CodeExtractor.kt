package com.example.sms2dingtalk

import java.util.regex.Pattern

object CodeExtractor {
    private val codeRegex = Pattern.compile(
        "(?i)(?:验证码|verification code|verify code|auth code|otp|code)[:\\s：-]{0,6}(\\d{4,8})|(?<!\\d)(\\d{4,8})(?!\\d)"
    )

    fun extractCode(content: String): String? {
        val matcher = codeRegex.matcher(content)
        if (!matcher.find()) return null
        return (matcher.group(1) ?: matcher.group(2) ?: "").ifBlank { null }
    }

    fun buildMessage(source: String, sender: String, code: String, raw: String): String {
        return "[验证码提醒]\n来源: $source $sender\n验证码: $code\n内容: ${raw.trim()}"
    }
}
