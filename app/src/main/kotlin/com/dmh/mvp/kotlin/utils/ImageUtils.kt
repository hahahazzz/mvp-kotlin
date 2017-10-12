/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmh.mvp.kotlin.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

import java.io.ByteArrayOutputStream

object ImageUtils {
    // public static String getThumbnailImagePath(String imagePath) {
    // String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
    // path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1,
    // imagePath.length());
    // EMLog.d("msg", "original image path:" + imagePath);
    // EMLog.d("msg", "thum image path:" + path);
    // return path;
    // }


    fun getScaledImg(r: Resources, resid: Int, reqWidth: Int,
                     reqHeight: Int): Bitmap {
        val options = Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(r, resid, options)
        val bWidth = options.outWidth
        val bHeight = options.outHeight
        if (bWidth > reqWidth || bHeight > reqHeight) {
            val ratioW = bWidth / reqWidth
            val ratioH = bHeight / reqHeight
            options.inSampleSize = if (ratioH >= ratioW) ratioH else ratioW
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(r, resid, options)

    }

    //drawable转bitmap
    fun drawableToBitmap(drawable: Drawable): Bitmap {


        val bitmap = Bitmap.createBitmap(

                drawable.intrinsicWidth,

                drawable.intrinsicHeight,

                if (drawable.opacity != PixelFormat.OPAQUE)
                    Bitmap.Config.ARGB_8888
                else
                    Bitmap.Config.RGB_565)

        val canvas = Canvas(bitmap)


        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        drawable.draw(canvas)

        return bitmap

    }

    fun Bitmap2Bytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    fun Bytes2Bitmap(b: ByteArray): Bitmap {

        val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
        return bitmap
    }
}
