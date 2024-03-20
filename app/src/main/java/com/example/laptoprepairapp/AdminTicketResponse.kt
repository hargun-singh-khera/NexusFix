package com.example.laptoprepairapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminTicketResponse : AppCompatActivity() {
    lateinit var etResponse: EditText
    lateinit var btnSubmit: Button
    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var userId: String
    lateinit var ticketId: String
    lateinit var laptopModel: String
    lateinit var problemDesc: String
    lateinit var tvLaptopModel: TextView
    lateinit var tvProblem: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_ticket_response)

        etResponse = findViewById(R.id.etResponse)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvLaptopModel = findViewById(R.id.tvLaptopModel)
        tvProblem = findViewById(R.id.tvProblem)
        imageView = findViewById(R.id.imageView)
        auth = FirebaseAuth.getInstance()

        userId = intent.getStringExtra("userId")!!
        ticketId = intent.getStringExtra("ticketId")!!
        laptopModel = intent.getStringExtra("laptopModel")!!
        problemDesc = intent.getStringExtra("problemDesc")!!

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Ticket ID: ${ticketId}")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Retrieve the ByteArray from the intent
        val byteArray = intent.getByteArrayExtra("bitmap")

        // Convert the ByteArray back to a Bitmap object
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        setValuesToView()

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")

        btnSubmit.setOnClickListener {
            submitResponse()
        }
    }

    private fun submitResponse() {
        val remarks = etResponse.text.toString()
        if (remarks.isEmpty()) {
            Toast.makeText(this@AdminTicketResponse, "Please enter a response", Toast.LENGTH_SHORT).show()
        }
        else {
            val request = RequestModel(userId, ticketId, laptopModel, problemDesc, remarks, true)
            dbRef.child(ticketId!!).setValue(request).addOnSuccessListener {
                Toast.makeText(this, "Your response has been recorded successfully", Toast.LENGTH_SHORT).show()
                finish()
            } .addOnFailureListener{ error ->
                Toast.makeText(this, "Error while responding ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
//        pushNotification(problemDesc, remarks)
    }

    private fun setValuesToView() {
        tvLaptopModel.text = "Laptop Model: " + laptopModel
        tvProblem.text = problemDesc
        imageView.setImageBitmap(bitmap)
    }


    @SuppressLint("MissingPermission")
    private fun pushNotification(title: String, body: String) {
        val channelId = "Channel Id"
        createNotificationChannel(channelId)
        Toast.makeText(this@AdminTicketResponse, "Executed 2", Toast.LENGTH_SHORT).show()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        Toast.makeText(this@AdminTicketResponse, "Executed 3", Toast.LENGTH_SHORT).show()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notificationBuilder)
    }

     fun createNotificationChannel(channelId: String) {
        val channelName = "Support Ticket"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
         Toast.makeText(this@AdminTicketResponse, "Executed 1", Toast.LENGTH_SHORT).show()
    }


}