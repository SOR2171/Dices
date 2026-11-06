# Dices

基于 Kotlin Multiplatform 与 Compose 的掷骰子应用。

![Dices](docs/images/Dices.png)

## 功能

- 混合投掷，可同时使用不同面数的骰子，默认组合为 2D6 + 1D20。
- 半透明玻璃面板、轻微背景柔化与高光边缘，完整适配背景图片。
- 骰子旋转与点数跳动，落定后显示每颗结果及总和。
- 骰组配置，支持常用面数、数量调整与自定义 2–1000 面骰子。
- 最近 50 次投掷记录，包括各颗点数、平均总点数与最高总点数。
- 宽窄窗口布局适配，窄窗口保留固定投掷按钮。

配置和历史仅在当前会话保留。每种骰子最多 20 颗，总数最多 60 颗。不同骰组的总点数范围不同，历史统计直接根据保留记录计算。

## Windows 运行

在全英文路径中打开项目，使用可运行 Gradle 的 JDK，执行下面的命令。

```powershell
.\gradlew.bat :desktopApp:run
```

已安装 Android Studio 的 Windows 环境也可运行根目录的 Run-Dices.cmd，它会优先使用 Android Studio 自带的 Java。

应用与打包名称均为 Dices。构建便携应用目录时执行下面的命令。

```powershell
.\gradlew.bat :desktopApp:createDistributable --no-configuration-cache
```

Windows 打包工具的下载任务需要关闭配置缓存。输出位于 desktopApp/build/compose/binaries/main/app/Dices，其中包含 Dices.exe、app 与 runtime。

## 验证

```powershell
.\gradlew.bat :desktopApp:compileKotlin :shared:jvmTest
```

公共逻辑包含 11 项测试，覆盖配置边界、结果范围、历史快照、重复点击与中断恢复。Windows 桌面编译、便携包启动和 Compose 语义交互已验证。Android 与 Web 入口保留，当前尚未完成构建及运行验证。

## 代码结构

公共代码位于 shared/src/commonMain/kotlin/io/github/sor2171/dices。

- App.kt 管理摄影背景、导航和投掷协程。
- core/DiceSession.kt 管理当前骰组、投掷结果和历史快照。
- ui/component/GlassSurface.kt 对齐背景坐标并绘制半透明玻璃面板。
- ui/component/TableComponents.kt 绘制骰子明暗面与桌面边饰。
- ui/screens 包含投掷、骰组配置和记录页面。
- ui/theme/DicesTheme.kt 定义深绿、黄铜与象牙色主题。

背景与图标随应用资源本地打包。

## 许可

MIT，见 LICENSE。
