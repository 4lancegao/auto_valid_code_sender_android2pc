package com.example.sms2dingtalk

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private val requiredPermissions by lazy {
        val perms = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        perms.toTypedArray()
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val denied = grants.filterValues { !it }
        if (denied.isNotEmpty()) {
            Toast.makeText(this, "权限未全部授予，短信监听可能失效", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

        val etWebhook = findViewById<EditText>(R.id.etWebhook)
        val etSecret = findViewById<EditText>(R.id.etSecret)
        val tvTip = findViewById<TextView>(R.id.tvTip)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnOpenNotificationSettings = findViewById<Button>(R.id.btnOpenNotificationSettings)

        etWebhook.setText(prefs.getString(PREF_WEBHOOK, ""))
        etSecret.setText(prefs.getString(PREF_SECRET, ""))

        val tips = StringBuilder()
        tips.append("1) 输入钉钉机器人 Webhook\n")
        tips.append("2) 如开启加签请输入 Secret（可选）\n")
        tips.append("3) 开启短信权限后可转发短信验证码\n")
        tips.append("4) 点击下方按钮开启通知监听权限，补抓应用内验证码（微信/支付类）\n")
        if (!isNotificationListenerEnabled()) {
            tips.append("   （当前未开启通知监听，应用内验证码可能抓不到）")
        }
        tvTip.text = tips.toString()

        btnSave.setOnClickListener {
            val webhook = etWebhook.text.toString().trim()
            val secret = etSecret.text.toString().trim()
            prefs.edit()
                .putString(PREF_WEBHOOK, webhook)
                .putString(PREF_SECRET, secret)
                .apply()
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
        }

        if (!hasAllPermissions()) {
            requestPermissions.launch(requiredPermissions)
        }

        btnOpenNotificationSettings.setOnClickListener {
            openNotificationListenerSettings()
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openNotificationListenerSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        if (flat.isNullOrBlank()) return false
        val target = ComponentName(this, NotificationForwardService::class.java).flattenToString()
        return flat.contains(target)
    }

    companion object {
        const val PREF_FILE = "dingtalk_forward"
        const val PREF_WEBHOOK = "pref_webhook"
        const val PREF_SECRET = "pref_secret"
    }
}
