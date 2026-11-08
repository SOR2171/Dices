package io.github.sor2171.dices.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter

private val supportedExtensions = arrayOf("png", "jpg", "jpeg", "bmp", "gif", "webp")

/** JVM 上把用户挑的图画存到 ~/.dices/wallpaper.img，扩展名不重要，解码时按内容判断 */
private val wallpaperFile: File
    get() = File(System.getProperty("user.home"), ".dices/wallpaper.img")

actual fun isWallpaperPickSupported(): Boolean = true

actual suspend fun pickWallpaperBytes(): ByteArray? = withContext(Dispatchers.IO) {
    var result: ByteArray? = null
    // 放到 EDT 上弹框；JFileChooser 用 null 作为 owner 时会挂到一个隐藏的共享窗口，
    // 这样窗口是模态的、不会像原来的 FileDialog 那样藏到主窗口后面
    SwingUtilities.invokeAndWait {
        val chooser = JFileChooser().apply {
            dialogTitle = "挑一张图片当背景"
            fileSelectionMode = JFileChooser.FILES_ONLY
            isAcceptAllFileFilterUsed = false
            fileFilter = FileNameExtensionFilter(
                "图片 (" + supportedExtensions.joinToString("/") + ")",
                *supportedExtensions
            )
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            val selected = chooser.selectedFile
            if (selected != null && selected.isFile) {
                result = runCatching { selected.readBytes() }.getOrNull()
            }
        }
    }
    result
}

actual suspend fun loadSavedWallpaperBytes(): ByteArray? = withContext(Dispatchers.IO) {
    runCatching { if (wallpaperFile.isFile) wallpaperFile.readBytes() else null }.getOrNull()
}

actual suspend fun saveWallpaperBytes(bytes: ByteArray) {
    withContext(Dispatchers.IO) {
        runCatching {
            wallpaperFile.parentFile?.mkdirs()
            wallpaperFile.writeBytes(bytes)
        }
    }
}

actual suspend fun clearSavedWallpaper() {
    withContext(Dispatchers.IO) {
        runCatching { wallpaperFile.delete() }
    }
}
