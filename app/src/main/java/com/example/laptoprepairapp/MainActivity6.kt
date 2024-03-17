package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity6 : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var cardViewTicketManagement: CardView
    lateinit var cardViewUserManagement: CardView
    lateinit var sharedPreferences: SharedPreferences
    var fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        auth = FirebaseAuth.getInstance()
        cardViewTicketManagement = findViewById(R.id.cardViewTicketManagement)
        cardViewUserManagement = findViewById(R.id.cardViewUserManagement)
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener {
//            if (!it.isSuccessful) {
//                Log.e("TokenDetails", "Token failed to recieve")
//                return@addOnCompleteListener
//            }
//            val token = it.result
//            Log.d("TOKEN", token)
//        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Dashboard")
        setSupportActionBar(toolbar)

        cardViewTicketManagement.setOnClickListener {
            startActivity(Intent(this, MainActivity7::class.java))
        }

        cardViewUserManagement.setOnClickListener {
            startActivity(Intent(this, MainActivity8::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.logout -> {
                val editor = sharedPreferences.edit()
                editor.putBoolean("isAdmin", false)
                editor.apply()
                auth.signOut()
                startActivity(Intent(this@MainActivity6, MainActivity2::class.java))
                finish()
            }
            R.id.notification -> {
                startActivity(Intent(this@MainActivity6, MainActivity7::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}