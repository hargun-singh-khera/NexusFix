package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class UserDashboard : AppCompatActivity() {
    lateinit var tvUserName: TextView
    lateinit var btnTrackReq: Button
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    lateinit var cardViewSupportTicket: CardView
    val fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Dashboard")
        setSupportActionBar(toolbar)

        tvUserName = findViewById(R.id.tvUserName)
        btnTrackReq = findViewById(R.id.btnTrackReq)

        getFCMToken()

        btnTrackReq.setOnClickListener {
            startActivity(Intent(this, UserTrackRequests::class.java))
        }

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        tvUserName.text = "Hi, " + userName.toString()

        Toast.makeText(this, "${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()

        cardViewSupportTicket = findViewById(R.id.cardViewSupportTicket)
        cardViewSupportTicket.setOnClickListener {
            startActivity(Intent(this, SupportTicket::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.profile -> {

            }
            R.id.logout -> {
                val editor = sharedPreferences.edit()
                editor.putBoolean("isAdmin", false)
                editor.apply()
                auth.signOut()
                startActivity(Intent(this@UserDashboard, LoginScreen::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.e("TokenDetails", "Token failed to recieve")
                return@addOnCompleteListener
            }
            val token = it.result
            Log.d("USER TOKEN", token)
        }
    }
}