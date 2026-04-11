# 验证码转发助手（Android）

这是一个把手机短信验证码和通知中的验证码（如微信/支付宝）转发到钉钉机器人的 Android App。

## 一键构建

### 本地构建（有 Android 工具链时）

```bash
cd "/Users/clawmac/Documents/New project"
if [ -x ./gradlew ]; then
  ./gradlew assembleDebug
else
  gradle assembleDebug
fi
```

APK 输出：
`app/build/outputs/apk/debug/app-debug.apk`

> 如果你看到 `A Gradle build's root directory should contain...` 错误，请确认你在项目根目录执行（包含 `settings.gradle.kts` 和 `build.gradle.kts`）。

### 云端构建（推荐）

已预置 GitHub Actions：`.github/workflows/build-debug-apk.yml`

1. 推送到 GitHub 仓库并运行 `Build Debug APK` 工作流（或触发 push 到 main）。
2. 在 Actions 的 Artifacts 下载 `debug-apk`。

## 使用

1. 安装并打开 App。
2. 输入并保存：
- 钉钉机器人 Webhook（必填）
- Secret（可选，机器人开启签名时可填）
3. 授权短信权限。
4. 点击“开启通知监听权限”，在系统设置里允许该应用的通知访问权（可选）。

## 说明

- `SmsReceiver`：监听系统短信并提取验证码
- `NotificationForwardService`：监听通知栏并提取验证码
- `CodeForwarder`：统一提取和转发逻辑
- `DingTalkSender`：发钉钉机器人 Webhook（支持签名）

## 边界说明

- 通知内容依赖系统通知中暴露的文本，不是每个 App 都一定可抓到完整验证码。
- 开启通知监听会提升抓取率，但有些应用会隐藏验证码内容。
