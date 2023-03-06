package com.example.jamapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import com.example.jamapp.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding



    lateinit var auth: FirebaseAuth
    private lateinit var mrefDB : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        var db : FirebaseDatabase? = null
        var datanoderef : DatabaseReference? = null
        val numero = findViewById<TextView>(R.id.tvnumero)
        val sendW = findViewById<MaterialButton>(R.id.btnsend)


      //VAR session start
        auth= FirebaseAuth.getInstance()



        var currentUser=auth.currentUser

        //        Reference
        val logout=findViewById<Button>(R.id.btnlogout)

        if(currentUser==null){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        sendW.setOnClickListener(View.OnClickListener {

            envioFinalW()
        })






        db = FirebaseDatabase.getInstance()
        datanoderef = db !!.getReference("db")
        datanoderef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    numero.text = "Disponibles: "+""+snapshot.child("ventas").value

                   var ventas = snapshot.child("ventas").value.toString().toInt()
                    var stock = snapshot.child("numero").value.toString().toInt()

                    //Creacion de grafica llamando al metodo
                   progrssFuncition(stock,ventas)





                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })





    }


    fun progrssFuncition(stock:Int, ventas:Int){

        val progressBlue = findViewById<CircularProgressBar>(R.id.progress_circular)
        progressBlue.apply {


           val valueProgressStock = stock.toString()
            val finalMaxProgressStock = valueProgressStock+"f"
            val MaxProgressConvertionStock = finalMaxProgressStock.toFloat()

            val valueProgressVentas = ventas.toString()
            val finalMaxProgressVentas = valueProgressVentas+"f"
            val MaxProgressConvertionVentas = finalMaxProgressVentas.toFloat()


            progressMax = MaxProgressConvertionStock
            setProgressWithAnimation(MaxProgressConvertionVentas,1000)
            progressBarColor = Color.parseColor("#0085ac")
            backgroundProgressBarColor = Color.parseColor("#94c9e4")

            progressBarWidth = 16f
            backgroundProgressBarWidth = 30f


        }


    }

    fun envioFinalW(){

       val uidphone =  auth.uid.toString()

        mrefDB = FirebaseDatabase.getInstance().getReference("phones")



        mrefDB.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

          SendWhatsapp(snapshot.child(uidphone).value.toString())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    fun SendWhatsapp(numero:String){

        // Creating intent with action send
        val intent = Intent(Intent.ACTION_VIEW)

        // Setting Intent type
        intent.type = "text/plain"

        val uri = "whatsapp://send?phone="+numero+"&text=Gracias por comprar con nacho xd"

        intent.setData(Uri.parse(uri))

        // Checking whether whatsapp is installed or not
        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this,
                "Please install whatsapp first.",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Starting Whatsapp
        startActivity(intent)
    }



    }


