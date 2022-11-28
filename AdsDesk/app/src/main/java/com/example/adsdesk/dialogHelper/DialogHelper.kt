package com.example.adsdesk.dialogHelper

import android.app.AlertDialog
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.adsdesk.MainActivity
import com.example.adsdesk.R
import com.example.adsdesk.accountHelper.AccountHelper
import com.example.adsdesk.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {
    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int){
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        setDialogState(index, rootDialogElement)

        val dialog = builder.create()
        rootDialogElement.btSignupIn.setOnClickListener {
            setOnClickSignUpIn(index, rootDialogElement,dialog)
        }
        rootDialogElement.btForgetPass.setOnClickListener {
            setOnClickResetPassword(rootDialogElement,dialog)
        }
        rootDialogElement.buttonGoogle.setOnClickListener {
            accHelper.signInWithGoogle()
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        if(rootDialogElement.edSignEmail.text.isNotEmpty()){
            act.myAuth.sendPasswordResetEmail("${rootDialogElement.edSignEmail.text}").addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(act,R.string.email_reset_password_was_sent,Toast.LENGTH_LONG).show()
                }
            }
            dialog?.dismiss()
        } else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        dialog?.dismiss()
        if(index == DialogConst.SIGN_UP_STATE){
            accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        } else {
            accHelper.signInWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        }
    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if(index == DialogConst.SIGN_UP_STATE){

            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.ac_signup)
            rootDialogElement.btSignupIn.text = act.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.ac_sign_in)
            rootDialogElement.btSignupIn.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.btForgetPass.visibility = View.VISIBLE
        }
    }
}