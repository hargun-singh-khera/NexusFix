package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class IntroductionScreen : AppCompatActivity() {
    lateinit var btnStart: Button
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    val fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false)
        Toast.makeText(this, "isAdmin: ${isAdmin}", Toast.LENGTH_SHORT).show()
        val currentUser = auth.currentUser
//        Toast.makeText(this, "${currentUser}", Toast.LENGTH_SHORT).show()
        val isVisited = sharedPreferences.getBoolean("homeVisited", false)

        if (currentUser != null) {
            if (isAdmin) {
                startActivity(Intent(this@IntroductionScreen, AdminDashboard::class.java))
            }
            else if (currentUser.isEmailVerified){
                startActivity(Intent(this@IntroductionScreen, UserDashboard::class.java))
            }
            else {
                startActivity(Intent(this@IntroductionScreen, LoginScreen::class.java))
                Toast.makeText(this@IntroductionScreen, "Please verify your email", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        else if (isVisited) {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

        btnStart.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("homeVisited", true)
            editor.apply()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }
}