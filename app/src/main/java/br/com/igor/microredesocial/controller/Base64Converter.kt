package br.com.igor.microredesocial.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import android.util.Base64

    class Base64Converter {

        companion object {

            fun drawableToString(drawable: Drawable): String {

                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                val imageBytes = outputStream.toByteArray()
                return Base64.encodeToString(imageBytes, Base64.DEFAULT)
            }

            fun stringToBitmap(imageString: String): Bitmap {

                val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }

        }

    }
