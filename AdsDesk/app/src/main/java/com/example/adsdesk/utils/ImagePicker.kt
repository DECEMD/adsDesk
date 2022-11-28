package com.example.adsdesk.utils

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.adsdesk.R
import com.example.adsdesk.act.EditAdsAct
import com.example.adsdesk.frag.ImageListFrag
import io.ak1.pix.PixFragment
import io.ak1.pix.databinding.FragmentPixBinding
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio
import io.ak1.pix.utility.ARG_PARAM_PIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    fun getOptions(imageCounter: Int) : Options{
        val options = Options().apply{
            ratio = Ratio.RATIO_AUTO                                     //Image/video capture ratio
            count = imageCounter                                                   //Number of images to restrict selection count
            spanCount = 3                                               //Number for columns in grid
            path = "Pix/Camera"                                         //Custom Path For media Storage
            isFrontFacing = false                                       //Front Facing camera on start
            mode = Mode.Picture                                              //Option to select only pictures or videos or both
            flash = Flash.Auto                                          //Option to select flash type
            preSelectedUrls = ArrayList<Uri>()                          //Pre selected Image Urls
        }
        return options
    }
// , containerId: Int, resultCallback: ((PixEventCallback.Results) -> Unit)?
    fun launchMultiSelectImage(context: EditAdsAct, containerId: Int, imageCounter: Int) {
    context.addPixToActivity(containerId, getOptions(imageCounter)) { result ->
        when (result.status) {
            PixEventCallback.Status.SUCCESS -> {
                getMultiSelectImage(context, result.data)
            }
            //PixEventCallback.Status.BACK_PRESSED -> // back pressed called
            else -> {}
        }
    }
}

    fun addImages(context: EditAdsAct, containerId: Int, imageCounter: Int) {
        context.addPixToActivity(containerId, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(context)
                    context.chooseImageFrag?.updateAdapter(result.data, context)
                }
                //PixEventCallback.Status.BACK_PRESSED -> // back pressed called
                else -> {}
            }
        }
    }

    fun launchSingleSelectImage(context: EditAdsAct, containerId: Int) {
        context.addPixToActivity(containerId, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(context)
                    getSingleSelectImage(context, result.data[0])
                }
                //PixEventCallback.Status.BACK_PRESSED -> // back pressed called
                else -> {}
            }
        }
    }

    private fun openChooseImageFrag(context: EditAdsAct){
        context.supportFragmentManager.beginTransaction().replace(R.id.placeHolder, context.chooseImageFrag!!).commit()
    }

    private fun closePixFrag(context: EditAdsAct){
        val fList = context.supportFragmentManager.fragments
        fList.forEach{
            if (it.isVisible){
                context.supportFragmentManager.beginTransaction().remove(it).commit()
            }
        }
    }
//        lateinit var returnValues: List<Uri>
        //context.addPixToActivity(containerId,getOptions(imageCount)){}
//        context.supportFragmentManager.beginTransaction()
//            .replace(containerId, PixFragment(resultCallback).apply {
//                arguments = Bundle().apply {
//                    putParcelable(ARG_PARAM_PIX, getOptions(imageCounter))
//                    returnValues = PixEventCallback.Results().data
//                }
//            }).commit()
       /* context.addPixToActivity(containerId, getOptions(imageCounter)){
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val returnedValue = PixEventCallback.Results().copy(it.data)
                    val returnedValue1 = PixEventCallback.Results().data
                    Log.d("MyLog", "openChooseImageFrag")
                    if (returnedValue.data.size > 1 && context.chooseImageFrag == null){
                        context.openChooseImageFrag(returnedValue.data)
                        Log.d("MyLog", "openChooseImageFrag2")
                    } else if (context.chooseImageFrag != null){
                        context.chooseImageFrag?.updateAdapter(returnedValue.data)
                    } else if (returnedValue.data.size == 1 && context.chooseImageFrag == null){                        Log.d("MyLog", "openChooseImageFrag2")
                        Log.d("MyLog", "openChooseImageFrag3")

                        CoroutineScope(Dispatchers.Main).launch {
                            val bitmapArray = ImageManager.imageResize(returnedValue.data, context)
                            context.imageAdapter.update(bitmapArray)

                        }
                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> context.onBackPressed()
            }
        }

        */


    fun getMultiSelectImage(context: EditAdsAct, uries: List<Uri>) {

        if (uries.size > 1 && context.chooseImageFrag == null) {
            context.openChooseImageFrag(uries)
        }  else if (uries.size == 1 && context.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch {
                context.binding.pBarLoad.visibility = View.VISIBLE
                val bitmapArray = ImageManager.imageResize(uries, context)
                context.binding.pBarLoad.visibility = View.GONE
                context.imageAdapter.update(bitmapArray)
                closePixFrag(context)
            }
        }
    }

    fun getSingleSelectImage(context: EditAdsAct, uri: Uri){
        context.chooseImageFrag?.setSingleImage(uri, context.editImagePos)
    }

}