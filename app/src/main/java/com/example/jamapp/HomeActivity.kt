package com.example.jamapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.jamapp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    // var autentication firebase sesion
    lateinit var auth: FirebaseAuth
    //var verification if
    lateinit var storedVerificationID : String
    //token recend
    lateinit var resentToken : PhoneAuthProvider.ForceResendingToken
    //call backs
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_home)

        //create instance of firebase auth

        auth = FirebaseAuth.getInstance()

        //Reference button login

        val btnlogin = findViewById<Button>(R.id.loginBtn)


        // if for know if exist user current

        var currentUser = auth.currentUser

        if(currentUser != null){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        // event buttton login

        btnlogin.setOnClickListener{
            login()
        }

        // Callback function for Phone Auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                startActivity(Intent(applicationContext,MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(applicationContext, "Error verifique datos", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationID = verificationId
                resentToken = token

                var intent = Intent(applicationContext,VerifyActivity::class.java)

                val mobileNumber = findViewById<EditText>(R.id.phoneNumber)
                var number = mobileNumber. text.toString().trim()





                // intent.putExtra("numero",number)
                intent.putExtra("storedVerificationId",storedVerificationID)
                intent.putExtra("phone", number)
                startActivity(intent)

            }


        }





    }


    private fun login(){

        //number mobile

        val mobileNumber = findViewById<EditText>(R.id.phoneNumber)
        var number = mobileNumber. text.toString().trim()

        if(!number.isEmpty()){
            number = "+52"+number


            sendVerificationcode(number)


        }else{
            Toast.makeText(this, "Ingrese numero telefono", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendVerificationcode(number: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }




}