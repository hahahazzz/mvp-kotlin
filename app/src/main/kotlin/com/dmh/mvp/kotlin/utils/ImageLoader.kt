package com.dmh.mvp.kotlin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

/**
 * @Author : QiuGang
 * @Email : 1607868475@qq.com
 * @Date : 2017/7/12 9:27
 */
class ImageLoader private constructor() {

    fun loadAvatar(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).transform(CenterInsideTransformation(imageView.context)).into(imageView)
    }

    fun loadFitCenter(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).fitCenter().into(imageView)
    }

    fun loadCenterCrop(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).centerCrop().into(imageView)
    }

    fun loadGif(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).asGif().into(imageView)
    }

    inner class CenterInsideTransformation : BitmapTransformation {
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

        private fun centerInside(recycle: Bitmap?, toCenter: Bitmap?, width: Int, height: Int): Bitmap? {
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
    }

    companion object {
        private val loader: ImageLoader by lazy { ImageLoader() }
        fun get(): ImageLoader = loader
    }
    /*private static final class CenterInsideTransformation extends BitmapTransformation {
        private static final String ID = "com.bumptech.glide.load.resource.bitmap.CenterInsideTransformation";
        private static volatile CenterInsideTransformation transformation;

        public static CenterInsideTransformation get() {
            if (transformation == null) {
                synchronized (CenterInsideTransformation.class) {
                    if (transformation == null) {
                        transformation = new CenterInsideTransformation();
                    }
                }
            }
            return transformation;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            final Bitmap toReuse = pool.get(outWidth, outHeight, toTransform.getConfig() != null
                    ? toTransform.getConfig() : Bitmap.Config.ARGB_8888);
            Bitmap transformed = centerInside(toReuse, toTransform, outWidth, outHeight);
            return transformed;
        }

        public static Bitmap centerInside(Bitmap recycle, Bitmap toCenter, int width, int height) {
            if (toCenter == null) {
                return null;
            }
            if (toCenter.getWidth() == width && toCenter.getHeight() == height) {
                return toCenter;
            }
            Matrix matrix = new Matrix();
            matrix.setScale(width * 1.0f / toCenter.getWidth(), height * 1.0f / toCenter.getHeight());
            final Bitmap result;
            if (recycle != null) {
                result = recycle;
            } else {
                result = Bitmap.createBitmap(width, height, toCenter.getConfig() != null ? toCenter
                        .getConfig() : Bitmap.Config.ARGB_8888);
            }
            result.setHasAlpha(toCenter.hasAlpha());
            Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(toCenter, matrix, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(ID.getBytes(Key.CHARSET));
        }
    }*/
}
