# 验证码转发助手（Android）

这是一个把手机短信验证码和通知中的验证码（如微信/支付宝）转发到钉钉机器人的 Android App 示例。

## 一键构建（云端推荐）

### 本地构建（有 Android 工具链时）

```bash
cd "/Users/clawmac/Documents/New project"
gradle assembleDebug
```

APK 输出：
`app/build/outputs/apk/debug/app-debug.apk`

> 如果你的环境有 `./gradlew`，也可以执行 `./gradlew assembleDebug`。

### 云端自动构建（推荐）

我已预置 GitHub Actions：`.github/workflows/build-debug-apk.yml`

1. 推送到 GitHub 仓库。
2. 运行 `Build Debug APK` 工作流（或触发 push 到 main）。
3. 在 Actions 的 Artifacts 下载 `debug-apk`。

## 使用

1. 安装并打开 App。
2. 输入并保存：
- 钉钉机器人 Webhook（必填）
- Secret（可选，机器人开启签名时必填）
3. 授权短信权限。
4. 点击“开启通知监听权限”，在系统里打开该应用的通知访问权（可选）。

## 工作流程

- `SmsReceiver`：监听系统短信，提取验证码
- `NotificationForwardService`：监听系统通知，提取验证码
- `CodeForwarder`：统一提取与转发逻辑
- `DingTalkSender`：调用钉钉机器人 Webhook（支持签名）

## 说明与边界

- 通知抓取依赖系统提供的通知内容文本，不是所有 App 的通知都暴露完整验证码文本。
- 通知监听需要用户手动在系统设置打开权限。
