package io.github.sor2171.dices.ui

/**
 * 壁纸这块唯一没法跨平台的部分：怎么让用户挑一张图、怎么记住它。
 *
 * commonMain 里只声明「要什么」，具体实现放在各自的平台源集：
 *   jvmMain    -> AWT 文件选择框 + 本地文件（~/.dices/wallpaper.img）
 *   wasmJsMain -> 浏览器里没有本地文件系统，直接降级成不支持
 */
expect fun isWallpaperPickSupported(): Boolean

/** 弹选择框让用户挑一张图，取消则返回 null；返回值是原始图片字节 */
expect suspend fun pickWallpaperBytes(): ByteArray?

/** 上次用过的壁纸字节，没有就返回 null */
expect suspend fun loadSavedWallpaperBytes(): ByteArray?

/** 记住这次挑的图 */
expect suspend fun saveWallpaperBytes(bytes: ByteArray)

/** 丢掉记住的图，回到默认壁纸 */
expect suspend fun clearSavedWallpaper()
