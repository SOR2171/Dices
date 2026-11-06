# DICES · 骰子
## 1.1.1

Windows 桌面版。采用你提供的牌桌照片作为场景背景，使用半透明玻璃面板、轻微背景柔化、高光边缘与象牙骰子。背景图按完整画面适配，窗口比例不同时会留出边距。

## 运行

Windows 便携包解压后，进入 Dices 文件夹，双击 Dices.exe。请保留同目录的 app 和 runtime 文件夹。

源码包解压到全英文路径，例如 C:\Projects\Dices，双击 Run-Dices.cmd。此方式会使用 Android Studio 自带的 Java，通过 Gradle 启动。

也可以在源码目录的 PowerShell 中运行下面的命令。

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :desktopApp:run
```

首次从源码运行需要下载 Gradle 和依赖。项目配置使用 Azul JDK 21 运行 Gradle daemon。Windows 项目路径应使用英文或数字，以通过 Android 插件的路径检查。

## 使用

- 混合投掷是唯一投掷模式。默认组合为两颗六面骰与一颗二十面骰。
- 点击“掷出骰子”开始投掷，约 580 毫秒后显示最终点数和总和。
- 点击“编辑骰组”或顶部“骰组配置”，添加面数、调整数量或移除骰子。
- 六面骰显示正面圆点，其他面数显示中央数值。台面的粒纹、边饰和背景照片用于营造场景。
- 投掷中切换到其他页面，本轮仍会完成。
- “投掷记录”保留最近 50 次结果，显示每颗点数、平均总点数与最高总点数。
- 窄窗口自动改为纵向布局，投掷按钮固定在内容下方。

配置和历史仅在本次打开期间保留。关闭程序后恢复默认组合。自定义面数范围为 2–1000，每种类型最多 20 颗，合计最多 60 颗。

## 源码

公共代码位于 shared/src/commonMain/kotlin/io/github/sor2171/dices。

| 文件 | 内容 |
|---|---|
| App.kt | 背景照片、明暗遮罩、正式导航和投掷协程 |
| core/DiceSession.kt | 配置、随机结果、历史快照和重复投掷保护 |
| ui/theme/DicesTheme.kt | 深绿、黄铜、象牙色主题 |
| ui/component/TableComponents.kt | 透明桌面边饰、骰子透视和明暗面 |
| ui/component/GlassSurface.kt | 背景坐标对齐、轻微柔化和玻璃高光 |
| ui/screens/RollScreen.kt | 混合投掷场景、窄屏固定按钮和总点数 |
| ui/screens/DiceLibraryScreen.kt | 骰组配置与输入校验 |
| ui/screens/HistoryScreen.kt | 历史结果和清空确认 |

背景图在 shared/src/commonMain/composeResources/drawable/table_background.png。该素材由你在本次任务中提供，随应用本地打包。Windows 初始窗口为 1440 × 960，可自行调整大小。

## 构建与测试

```powershell
.\gradlew.bat :desktopApp:compileKotlin :shared:jvmTest
.\gradlew.bat :desktopApp:createDistributable --no-configuration-cache
```

第二条命令生成 Windows 应用目录，首次执行可能需要下载打包工具。数据层测试共 11 项，覆盖输入边界、历史快照、重复点击和中断恢复。

Android 与 Web 项目入口同时保留。当前正式交付及验证范围为 Windows，移动端和 Web 尚未完成构建与运行验证。

## 来源

基于 SOR2171 的 Dices 和你提供的本地项目继续开发。原项目 MIT 许可见 LICENSE。素材来源和本次验证范围见 VALIDATION.md。
