package io.github.sor2171.dices.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.util.Properties
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

/**
 * 壁纸的读写：内置默认壁纸、用户自己挑的图片、上次用过的路径。
 */
object WallpaperStore {
    private val configFile: File
        get() = File(System.getProperty("user.home"), ".dices/settings.properties")

    private val supportedExtensions = setOf("png", "jpg", "jpeg", "bmp", "gif")

    /** 打包进程序里的默认壁纸 */
    fun loadBuiltIn(): ImageBitmap? = runCatching {
        val stream = WallpaperStore::class.java.getResourceAsStream("/wallpaper.jpg") ?: return null
        stream.use { ImageIO.read(it)?.toComposeImageBitmap() }
    }.getOrNull()

    /** 启动时用：优先上次那张，其次内置默认图 */
    fun initial(): Pair<ImageBitmap, String>? {
        val savedFile = loadSavedPath()?.let(::File)
        if (savedFile != null) {
            val bitmap = runCatching { ImageIO.read(savedFile)?.toComposeImageBitmap() }.getOrNull()
            if (bitmap != null) return bitmap to savedFile.name
        }
        val builtIn = loadBuiltIn() ?: return null
        return builtIn to "默认壁纸"
    }

    fun isSupported(file: File): Boolean =
        file.isFile && file.extension.lowercase() in supportedExtensions

    suspend fun load(file: File): ImageBitmap? = withContext(Dispatchers.IO) {
        runCatching {
            if (!isSupported(file)) return@runCatching null
            ImageIO.read(file)?.toComposeImageBitmap()
        }.getOrNull()
    }

    /** 弹系统文件选择框挑一张图（阻塞，放在 IO 线程调用） */
    suspend fun chooseImage(): File? = withContext(Dispatchers.IO) {
        var chosen: File? = null
        SwingUtilities.invokeAndWait {
            val dialog = FileDialog(null as Frame?, "挑一张图片当背景", FileDialog.LOAD)
            dialog.filenameFilter = FilenameFilter { _, name ->
                name.substringAfterLast('.', "").lowercase() in supportedExtensions
            }
            dialog.isVisible = true
            val name = dialog.file
            val directory = dialog.directory
            if (name != null && directory != null) chosen = File(directory, name)
            dialog.dispose()
        }
        chosen
    }

    fun loadSavedPath(): String? = runCatching {
        if (!configFile.isFile) return null
        val properties = Properties()
        configFile.inputStream().use { properties.load(it) }
        properties.getProperty("wallpaper")?.takeIf { it.isNotBlank() && File(it).isFile }
    }.getOrNull()

    fun savePath(file: File) {
        runCatching {
            configFile.parentFile?.mkdirs()
            val properties = Properties()
            if (configFile.isFile) configFile.inputStream().use { properties.load(it) }
            properties.setProperty("wallpaper", file.absolutePath)
            configFile.outputStream().use { properties.store(it, "Dices settings") }
        }
    }
}
