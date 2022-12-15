package net.sgztech.timeboat.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import net.sgztech.timeboat.util.GlideCacheUtils
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import kotlin.Throws
import com.imlaidian.utilslibrary.UtilsApplication
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.imlaidian.utilslibrary.utils.ImageUtil
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.util.GlideCacheUtils.Companion.getFormatSize
import java.io.File
import java.lang.Exception
import java.math.BigDecimal

class GlideCacheUtils {
    /**
     * 清除图片磁盘缓存
     */
    fun clearImageDiskCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread {
                    Glide.get(context!!).clearDiskCache()
                    // BusUtil.getBus().post(new GlideCacheClearSuccessEvent());
                }.start()
            } else {
                Glide.get(context!!).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context!!).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片所有缓存
     */
    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val ImageExternalCatchDir =
            context.externalCacheDir.toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(ImageExternalCatchDir, true)
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    fun getCacheSize(context: Context): String {
        try {
            return getFormatSize(getFolderSize(File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath filePath
     * @param deleteThisPath deleteThisPath
     */
    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().size == 0) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val TAG = GlideCacheUtils::class.java.simpleName
        var instance: GlideCacheUtils? = null
            get() {
                if (field == null) {
                    synchronized(GlideCacheUtils::class.java) {
                        if (field == null) {
                            field = GlideCacheUtils()
                        }
                    }
                }
                return field
            }
            private set

        /**
         * 格式化单位
         *
         * @param size size
         * @return size
         */
        private fun getFormatSize(size: Double): String {
            val kiloByte = size / 1024
            if (kiloByte < 1) {
                return size.toString() + "Byte"
            }
            val megaByte = kiloByte / 1024
            if (megaByte < 1) {
                val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
            }
            val gigaByte = megaByte / 1024
            if (gigaByte < 1) {
                val result2 = BigDecimal(java.lang.Double.toString(megaByte))
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
            }
            val teraBytes = gigaByte / 1024
            if (teraBytes < 1) {
                val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
            }
            val result4 = BigDecimal(teraBytes)
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
        }

        //    public void clearnImageLoadFile() {
        //
        //        File cacheDir = StorageUtils.getCacheDirectory(UtilsApplication.getInstance().getApplicationContext(), true);
        //        File individualDir = new File(cacheDir, "uil-images");
        //
        //        if (individualDir.exists()) {
        //
        //            boolean delete = FileUtils.deleteDirectoryByPath(individualDir.getAbsolutePath());
        //
        //            LogUtil.d(TAG, "clearnImageLoadFile delete imageLoad file +delete=" +delete);
        //        } else {
        //            LogUtil.d(TAG, "uil-images file not exist");
        //        }
        //
        //    }
        fun glideSetBackground(id: Int, targetView: View?) {
            if (targetView == null) {
                LogUtil.d("GlideCacheUtils", "glideSetBackground targetView is null return")
                return
            }
            val mContext = UtilsApplication.getInstance().applicationContext
            LogUtil.d("GlideCacheUtils", "id=$id")
            try {
                Glide.with(mContext).load(id)
                    .into(object : CustomViewTarget<View?, Drawable?>(targetView) {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            LogUtil.d("GlideCacheUtils", "onLoadFailed,id:$id")
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable?>?
                        ) {
                            if (targetView != null) {
                                targetView.background = resource
                            }
                        }

                        override fun onResourceCleared(placeholder: Drawable?) {}
                    })
            } catch (e: OutOfMemoryError) {
                LogUtil.d(
                    "GlideCacheUtils",
                    "setViewGroupLayoutBg OutOfMemoryError e=" + e.localizedMessage
                )
                if (targetView != null) {
                    targetView.background = ImageUtil.compressDrableByResId(id)
                }
            } catch (e: Exception) {
                LogUtil.d("GlideCacheUtils", "setViewGroupLayoutBg Exception e=$e")
            }
        }
    }
}