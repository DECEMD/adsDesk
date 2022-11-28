package com.example.adsdesk.act

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.adsdesk.R
import com.example.adsdesk.databinding.ActivityFilterBinding
import com.example.adsdesk.dialogs.DialogSpinnerHelper
import com.example.adsdesk.utils.CityHelper

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickSelectCountry()
        onClickSelectCity()
        actionBarSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
    private fun onClickSelectCountry() = with(binding){
        tvPickCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCounties(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, binding.tvPickCountry)
            if (binding.tvPickCity.text.toString() != getString(R.string.select_city)){
                binding.tvPickCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding){
        tvPickCity.setOnClickListener {
            val selectedCountry = binding.tvPickCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, binding.tvPickCity)
            } else {
                Toast.makeText(this@FilterActivity, "No country selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickDone() = with(binding){
        btDone.setOnClickListener {

        }
    }

    fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}