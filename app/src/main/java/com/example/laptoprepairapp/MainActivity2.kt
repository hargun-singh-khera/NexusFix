package com.example.laptoprepairapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity2 : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPass: EditText
    lateinit var btnSignin: Button
    lateinit var tvRegister: TextView
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        btnSignin = findViewById(R.id.btnSignin)
        tvRegister = findViewById(R.id.tvRegister)
        auth = FirebaseAuth.getInstance()

        btnSignin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }
    }
    private fun loginUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
        else {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(this@MainActivity2, "Login Successful", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@MainActivity2, "Unable to Login", Toast.LENGTH_SHORT).show()
                }
            }
        }
        startActivity(Intent(this, MainActivity4::class.java))
    }
}