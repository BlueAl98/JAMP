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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import java.util.*

class VerifyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    lateinit var auth : FirebaseAuth
    private lateinit var mrefDB : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_verify)


        auth = FirebaseAuth.getInstance()

        val storedVerificationId=intent.getStringExtra("storedVerificationId")


      //  Toast.makeText(this, storedVerificationId.toString(), Toast.LENGTH_SHORT).show()

        //Reference




        val verify = findViewById<Button>(R.id.verifyBtn)
        val otpGiven = findViewById<EditText>(R.id.id_otp)

        verify.setOnClickListener{
            var otp =  otpGiven.text.toString().trim()
            if(!otp.isEmpty()){


                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)

                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()



            }
        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val phoneSend=intent.getStringExtra("phone")

                   Log.d("LOG",phoneSend.toString())
                    addnumbersDatabase(phoneSend.toString())



                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
// ...
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid



                        Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show()



                    }
                }
            }

    }

    private fun addnumbersDatabase(numero:String){


        mrefDB = FirebaseDatabase.getInstance().getReference("phones")


        mrefDB.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child("+52"+numero).exists()){
                    Log.d("LOG","exite ese numero")



                }else{


                     mrefDB.child(auth.uid.toString()).setValue("+52"+numero)

                    Log.d("LOG","No exite")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })




    }



}