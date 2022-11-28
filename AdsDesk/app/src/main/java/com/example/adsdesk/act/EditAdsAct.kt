package com.example.adsdesk.act

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.adsdesk.MainActivity
import com.example.adsdesk.R
import com.example.adsdesk.adapters.ImageAdapter
import com.example.adsdesk.model.Ad
import com.example.adsdesk.model.DbManager
import com.example.adsdesk.databinding.ActivityEditAdsBinding
import com.example.adsdesk.dialogs.DialogSpinnerHelper
import com.example.adsdesk.frag.FragmentCloseInterface
import com.example.adsdesk.frag.ImageListFrag
import com.example.adsdesk.utils.CityHelper
import com.example.adsdesk.utils.ImageManager
import com.example.adsdesk.utils.ImageManager.fillImageArray
import com.example.adsdesk.utils.ImagePicker
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    lateinit var binding: ActivityEditAdsBinding
    private var dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var chooseImageFrag: ImageListFrag? = null
    private val dbManager = DbManager()
    var editImagePos = 0
    private var imageIndex = 0
    private var isEditState: Boolean = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkEditState()
        imageChangeImageCounter()


//For spinner
//        val adapter = ArrayAdapter(this,
//            android.R.layout.simple_spinner_item,
//            CityHelper.getAllCounties(this))
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spCountry.adapter = adapter
    }

    private fun checkEditState(){
        if(isEditState()){
            isEditState = true
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if (ad != null) fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad: Ad) = with(binding){
        tvPickCountry.text = ad.country
        tvPickCity.text = ad.city
        editTel.setText(ad.tel)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
        fillImageArray(ad, imageAdapter)
    }


    private fun init(){
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
    }

    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCounties(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvPickCountry)
        if (binding.tvPickCity.text.toString() != getString(R.string.select_city)){
            binding.tvPickCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View){
        val selectedCountry = binding.tvPickCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvPickCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun onCLickSelectCategory(view: View){
        val listCat = resources.getStringArray(R.array.category).toMutableList() as ArrayList<String>
        dialog.showSpinnerDialog(this, listCat, binding.tvCat)
    }

    fun onClickPublish(view:View){
        ad = fillAd()
        if (isEditState){
            ad?.copy(key = ad?.key)?.let { dbManager.publishAd(it, onPublishFinish()) }
        } else {
            uploadImages()
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener{
        return object: DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }

        }
    }

    private fun fillAd(): Ad{
        val ad: Ad
        binding.apply {
            ad = Ad(tvPickCountry.text.toString(),
            tvPickCity.text.toString(),
            editTel.text.toString(),
            edIndex.text.toString(),
            checkBoxWithSend.isChecked.toString(),
            tvCat.text.toString(),
            edTitle.text.toString(),
            edPrice.text.toString(),
            edDescription.text.toString(),
            editEmail.text.toString(),
                "empty",
                "empty",
                "empty",
            dbManager.db.push().key,
            dbManager.auth.uid, System.currentTimeMillis().toString(),"0")
        }
        return ad
    }

    fun openChooseImageFrag(newList: List<Uri>?) {
        chooseImageFrag = ImageListFrag(this)
        if (newList != null)chooseImageFrag?.resizeSelectedImages(newList, true, this)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeHolder, chooseImageFrag!!).commit()
    }


    fun onClickGetImages(view: View) {
        if (imageAdapter.mainArray.size == 0){
            ImagePicker.launchMultiSelectImage(this, R.id.placeHolder, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
        /*addPixToActivity(R.id.placeHolder, ImagePicker.getOptions(3)) {

            when (it.status) {

                PixEventCallback.Status.SUCCESS -> {
                    val returnedValue = PixEventCallback.Results().copy(it.data)
                    Log.d("MyLog", "${returnedValue}")
                    val folderPath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val separator = File.separator + "appFolderName"
                    Log.d("MyLog", "${folderPath}")
                    Log.d("MyLog", "${separator}")


//                    Log.d("MyLog", (it.data[0])::class.java.simpleName)
//returnedValue.data.size > 1
                    if (chooseImageFrag == null) {
                        chooseImageFrag = ImageListFrag(this, it.data)
                        Log.d("MyLog", "${returnedValue.data.size}")
                        val fm = supportFragmentManager.beginTransaction()
                        fm.replace(R.id.placeHolder, chooseImageFrag!!).commit()

                    } else if (chooseImageFrag != null) {
                        chooseImageFrag?.updateAdapter(it.data)
                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> onBackPressed()
            }
        }
*/
    }

    private fun uploadImages(){
        if (imageAdapter.mainArray.size == imageIndex){
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
        uploadImage(byteArray){
            //dbManager.publishAd(ad!!, onPublishFinish())
            nextImage(it.result.toString())
        }
    }
    private fun nextImage(uri: String){
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }
    private fun setImageUriToAd(uri: String){
        when(imageIndex){
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }
//подготавливаем картинку к байтэррей
    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray{
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>){
        val imStorageRef = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask{
            imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeImageCounter(){
        binding.vpImages.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.vpImages.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }

    override fun onFragClose(list: ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFrag = null
    }
}