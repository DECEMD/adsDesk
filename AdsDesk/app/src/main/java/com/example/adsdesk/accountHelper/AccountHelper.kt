package com.example.adsdesk.accountHelper


import android.widget.Toast
import com.example.adsdesk.MainActivity
import com.example.adsdesk.R
import com.example.adsdesk.constants.FirebaseAuthConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import kotlin.Exception

class AccountHelper(private val act: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.currentUser?.delete()?.addOnCompleteListener {
                if (it.isSuccessful){
                    act.myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it2 ->
                        if (it2.isSuccessful) {
                            signUpIsSuccessful(it2.result.user!!)
//                    sendEmailVerification(it.result.user!!)
//                    act.uiUpdate(it.result.user)
                        } else {
                            signUpWithEmailExceptions(it.exception!!,email, password)
                        }
                    }
                }
            }
        }
    }

    private fun signUpIsSuccessful(user: FirebaseUser){
        sendEmailVerification(user!!)
        act.uiUpdate(user)
    }

    private fun signUpWithEmailExceptions(e: Exception, email: String, password: String ){
        if (e is FirebaseAuthUserCollisionException) {
            val exception = e as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToG(email,password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            val exception = e as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {

            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            val exception = e as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {

            }
        }
    }

    private fun signInWithEmailExceptions(e: Exception, email: String, password: String){
        if (e is FirebaseAuthInvalidCredentialsException) {
            val exception = e as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {

            } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {

            }
        } else if (e is FirebaseAuthInvalidUserException){
            val exception = e as FirebaseAuthInvalidUserException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND){
                TODO("dobavit Toast")
            }
        }
    }

    fun signInWithEmail(email: String, password: String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            act.myAuth.currentUser?.delete()?.addOnCompleteListener {
                if (it.isSuccessful){
                    act.myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { it2->
                        if (it2.isSuccessful){
                            act.uiUpdate(it2.result.user)
                        } else {
                            signInWithEmailExceptions(it2.exception!!,email, password)
                        }
                    }
                }
            }
        }
    }

    private fun linkEmailToG(email: String,password: String){
        val credential = EmailAuthProvider.getCredential(email, password)
        if(act.myAuth.currentUser != null) {
            act.myAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(act, R.string.link_done, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(act, R.string.enter_google, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act,gso)
    }

    fun signInWithGoogle(){
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.googleSignInLauncher.launch(intent)
        }

    fun signOutG(){
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.myAuth.currentUser?.delete()?.addOnCompleteListener { it2 ->
            if (it2.isSuccessful) {
                act.myAuth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(act, "Sign in is Done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(it.result.user)
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(act,
                    act.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act,
                    act.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInAnonymously(listener: Listener){
        act.myAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful){
                listener.onComplete()
                Toast.makeText(act,"Вы вошли как гость", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act,"Неудалось войти как гость", Toast.LENGTH_SHORT).show()
            }
        }
    }
    interface Listener{
        fun onComplete()
    }

}