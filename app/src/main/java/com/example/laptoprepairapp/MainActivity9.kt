package com.example.laptoprepairapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity9 : AppCompatActivity() {
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
        setContentView(R.layout.activity_main9)

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



//        Toast.makeText(this, "Ticket Id: ${ticketId}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "Model: ${laptopModel}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "Problem: ${problemDesc}", Toast.LENGTH_SHORT).show()

        setValuesToView()

        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")

        btnSubmit.setOnClickListener {
            submitResponse()
        }
    }

    private fun submitResponse() {
        val remarks = etResponse.text.toString()
        val request = RequestModel(userId, ticketId, laptopModel, problemDesc, remarks, true)
        dbRef.child(ticketId!!).setValue(request).addOnSuccessListener {
            Toast.makeText(this, "Response recorded successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity7::class.java))
        } .addOnFailureListener{ error ->
            Toast.makeText(this, "Error while responding ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setValuesToView() {
        tvLaptopModel.text = laptopModel
        tvProblem.text = problemDesc
//        imageView.setImageBitmap(bitmap)
    }
}