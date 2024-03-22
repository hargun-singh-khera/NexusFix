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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class UserDashboard : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fab: FloatingActionButton
    lateinit var toolbar: Toolbar
    lateinit var pass: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_dashboard)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Dashboard")
        setSupportActionBar(toolbar)

        replaceFrameWithFragment(HomeFragment())

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)

        bottomNavHandler()

        auth = FirebaseAuth.getInstance()

        fab.setOnClickListener {
            shareApp()
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
                replaceFrameWithFragment(ProfileFragment())
                toolbar.setTitle("My Profile")
            }
            R.id.resetPassword -> {
                startActivity(Intent(this@UserDashboard, ResetPassword::class.java))
            }
            R.id.logout -> {
                logoutAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun replaceFrameWithFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.replace(R.id.frameLayout, fragment)
        fragTransaction.commit()
    }

    private fun bottomNavHandler() {
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    replaceFrameWithFragment(HomeFragment())
                    toolbar.setTitle("Dashboard")
                }
                R.id.rate -> {
                    toolbar.setTitle("Rate Us")
                    replaceFrameWithFragment(RateFragment())
                }
                R.id.profile -> {
                    replaceFrameWithFragment(ProfileFragment())
                    toolbar.setTitle("My Profile")
                }
                R.id.logout -> logoutAlert()
            }
            true
        }
    }

    fun logoutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout ?")
        builder.setTitle("Logout Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> logoutRedirect()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun logoutRedirect() {
        auth.signOut()
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT,"https://drive.google.com/file/d/1eoH8RwKV_TtW6mXETQFhNrBHguD4gqGa/view?usp=sharing")
        startActivity(Intent.createChooser(intent, "Share Link!"))
    }

}