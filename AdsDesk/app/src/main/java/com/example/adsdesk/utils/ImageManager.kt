package com.example.adsdesk.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toIcon
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.example.adsdesk.adapters.ImageAdapter
import com.example.adsdesk.model.Ad

import com.example.adsdesk.utils.ImageManager.MAX_IMAGE_SIZE
import com.example.adsdesk.utils.ImageManager.getImageSize
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream


object ImageManager {
    const val MAX_IMAGE_SIZE = 1280

    fun getImageSize(uri: Uri, act: Activity) : List<Int>{
        val inStream = act.contentResolver.openInputStream(uri)
        //создаем файл темп.тмп в кеше и оно постоянно перезаписывается, так что не захламится ничего
//        val tempFile = File(act.cacheDir, "temp.tmp")
//        if (inStream != null) {
//            tempFile.copyInStreamToFile(inStream)
//        }
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
//        BitmapFactory.decodeFile(tempFile.path, options)
        BitmapFactory.decodeStream(inStream, null, options)
        return  listOf(options.outWidth, options.outHeight)
//        return if (imageRotation(tempFile) == 90)
//            listOf(options.outHeight, options.outWidth)
//        else listOf(options.outWidth, options.outHeight)
    }
//    //extension функция чтобы получить реальную ссылку через contentResolve(которая присылает из пикс ссылки)
//    private fun File.copyInStreamToFile(inputStream: InputStream){
//        //use делает функцию - закрытие и открытие потока чтобы самим это не делать
//        this.outputStream().use {
//            inputStream.copyTo(it)
//        }
//    }

//    private fun imageRotation(imageFile: File): Int{
//        var rotation = 0
//        val exif = ExifInterface(imageFile.absolutePath)
//
//        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//        rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270){
//            90
//        } else {
//            0
//        }
//        return rotation
//    }

    fun chooseScaleType(im: ImageView, bitmap: Bitmap){
        if (bitmap.width > bitmap.height){
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            im.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    suspend fun imageResize(uries: List<Uri>, act: Activity): MutableList<Bitmap> = withContext(Dispatchers.IO){
        val tempList = ArrayList<List<Int>>()
        val bitmapList = mutableListOf<Bitmap>()
        for (i in uries.indices){
            val size = getImageSize(uries[i], act)
            val imageRatio = size[0].toFloat() / size[1].toFloat()
            if(imageRatio > 1){
                if (size[0] > MAX_IMAGE_SIZE){
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                } else {
                    tempList.add(listOf(size[0], size[1]))
                }
            } else {
                if (size[1] > MAX_IMAGE_SIZE){
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                } else {
                    tempList.add(listOf(size[0], size[1]))
                }
            }
        }
        Log.d("MyLog", "$uries")
        for (i in uries.indices){

            var e = kotlin.runCatching {
                bitmapList.add(Picasso.get().load(uries[i]).resize(tempList[i][0], tempList[i][1]).get())
            }
            Log.d("MyLog", "${e.isSuccess}")
        }
        Log.d("MyLog", "$bitmapList")

        return@withContext bitmapList
    }

    private suspend fun getBitmapFromUries(uries: List<String?>): MutableList<Bitmap> = withContext(Dispatchers.IO){
        val bitmapList = mutableListOf<Bitmap>()
        for (i in uries.indices){
            var e = kotlin.runCatching {
                bitmapList.add(Picasso.get().load(uries[i]).get())
            }
        }
        return@withContext bitmapList
    }

    fun fillImageArray(ad: Ad, adapter: ImageAdapter){
        val listUries = listOf(ad.mainImage, ad.image2, ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = getBitmapFromUries(listUries)
            adapter.update(bitmapList)
        }
    }
}

//                        bitmapList.add(Picasso.get()
//                    .load(uries[i])
//                    .resize(tempList[i][0], tempList[i][1])
//                    .get())