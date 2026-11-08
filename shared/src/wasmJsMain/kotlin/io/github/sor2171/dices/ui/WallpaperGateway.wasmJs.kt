package io.github.sor2171.dices.ui

/**
 * 浏览器里没有随便读本地文件的权限（只能通过 <input type=file> 且要用户主动点），
 * 这一版先明确降级：只提供打包进程序的默认壁纸。
 */
actual fun isWallpaperPickSupported(): Boolean = false

actual suspend fun pickWallpaperBytes(): ByteArray? = null

actual suspend fun loadSavedWallpaperBytes(): ByteArray? = null

actual suspend fun saveWallpaperBytes(bytes: ByteArray) = Unit

actual suspend fun clearSavedWallpaper() = Unit
