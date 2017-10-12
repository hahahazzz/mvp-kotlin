package com.dmh.mvp.kotlin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.dmh.mvp.kotlin.R
import java.security.MessageDigest

/**
 * @Author : QiuGang
 * @Email : 1607868475@qq.com
 * @Date : 2017/7/12 9:27
 */
class ImageLoader private constructor() {
    private val requestOption: RequestOptions by lazy {
        RequestOptions().apply {
            placeholder(R.mipmap.ic_launcher)
            error(R.mipmap.ic_launcher)
            diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        }
    }

    fun loadAvatar(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(requestOption.transform(CenterInsideTransformation())).into(imageView)
    }

    fun load(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }

    fun loadFitCenter(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(requestOption.fitCenter()).into(imageView)
    }

    fun loadCenterCrop(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(requestOption.centerCrop()).into(imageView)
    }

    /*inner class CenterInsideTransformation : BitmapTransformation {
        constructor(context: Context) : super(context) {}

        constructor(bitmapPool: BitmapPool) : super(bitmapPool) {}

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            val toReuse = pool.get(outWidth, outHeight, if (toTransform.config != null)
                toTransform.config
            else
                Bitmap.Config.ARGB_8888)
            val transformed = centerInside(toReuse, toTransform, outWidth, outHeight)
            return transformed
        }

        override fun getId(): String {
            return "CenterInsideTransformation.com.bumptech.glide.load.resource.bitmap"
        }

        fun centerInside(recycle: Bitmap?, toCenter: Bitmap?, width: Int, height: Int): Bitmap? {
            if (toCenter == null) {
                return null
            }
            if (toCenter.width == width && toCenter.height == height) {
                return toCenter
            }
            val matrix = Matrix()
            matrix.setScale(width * 1.0f / toCenter.width, height * 1.0f / toCenter.height)
            val result: Bitmap
            if (recycle != null) {
                result = recycle
            } else {
                result = Bitmap.createBitmap(width, height, if (toCenter.config != null) toCenter.config else Bitmap.Config.ARGB_8888)
            }
            result.setHasAlpha(toCenter.hasAlpha())
            val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
            val canvas = Canvas(result)
            canvas.drawBitmap(toCenter, matrix, paint)
            return result
        }
    }*/

    companion object {
        private val loader: ImageLoader by lazy { ImageLoader() }
        fun get(): ImageLoader = loader
    }

    internal class CenterInsideTransformation : BitmapTransformation() {
        override fun updateDiskCacheKey(messageDigest: MessageDigest?) {
        }

        val id = "CenterInsideTransformation.com.bumptech.glide.load.resource.bitmap"

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            val toReuse = pool.get(outWidth, outHeight, if (toTransform.config != null)
                toTransform.config
            else
                Bitmap.Config.ARGB_8888)
            return centerInside(toReuse, toTransform, outWidth, outHeight)
        }


        fun centerInside(recycle: Bitmap?, toCenter: Bitmap?, width: Int, height: Int): Bitmap? {
            if (toCenter == null) {
                return null
            }
            if (toCenter.width == width && toCenter.height == height) {
                return toCenter
            }
            val matrix = Matrix()
            matrix.setScale(width * 1.0f / toCenter.width, height * 1.0f / toCenter.height)
            val result: Bitmap = recycle ?: Bitmap.createBitmap(width, height, if (toCenter.config != null)
                toCenter.config
            else
                Bitmap.Config.ARGB_8888)
            result.setHasAlpha(toCenter.hasAlpha())
            val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
            val canvas = Canvas(result)
            canvas.drawBitmap(toCenter, matrix, paint)
            return result
        }
    }
}
