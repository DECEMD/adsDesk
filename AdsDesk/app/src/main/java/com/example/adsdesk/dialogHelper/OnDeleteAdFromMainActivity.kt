package com.example.adsdesk.dialogHelper

import android.app.Activity
import android.app.AlertDialog
import com.example.adsdesk.MainActivity
import com.example.adsdesk.databinding.DeleteAdOrNotBinding
import com.example.adsdesk.databinding.ProgressDialogLayoutBinding
import com.example.adsdesk.model.Ad

object OnDeleteAdFromMainActivity {
    fun createProgressDialog(act: MainActivity, ad: Ad): AlertDialog {
        val builder = AlertDialog.Builder(act)
        val binding = DeleteAdOrNotBinding.inflate(act.layoutInflater)
        val view = binding.root
        builder.setView(view)
        val dialog = builder.create()

        binding.ibNot.setOnClickListener {
            dialog.dismiss()
        }
        binding.ibYes.setOnClickListener {
            act.firebaseViewModel.deleteItem(ad)
            dialog.dismiss()
        }
        return dialog
    }


}