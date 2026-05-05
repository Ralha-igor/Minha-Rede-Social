package br.com.igor.microredesocial.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream

class Base64Converter {

    companion object {

        fun drawableToString(drawable: Drawable): String {

            val bitmapDrawable = drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap

            val outputStream = ByteArrayOutputStream()

            // 🔥 COMPRESSÃO REAL (ANTES ERA PNG 100)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            val imageBytes = outputStream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }

        fun stringToBitmap(imageString: String): Bitmap? {
            return try {
                val base64Puro = imageString.removePrefix("data:image/jpeg;base64,")
                val imageBytes = Base64.decode(base64Puro, Base64.DEFAULT)

                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2
                }

                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            } catch (e: Exception) {
                null
            }
        }
    }
}