package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class OTPVerification : AppCompatActivity() {
    lateinit var etOtp: EditText
    lateinit var btnVerify: Button
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    lateinit var storedVerificationId:String
    private lateinit var dbRef: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    val fileName = "userType"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        etOtp = findViewById(R.id.etOtp)
        btnVerify = findViewById(R.id.btnVerify)
        auth = FirebaseAuth.getInstance()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)

        storedVerificationId = intent.getStringExtra("storedVerificationId")!!
        Toast.makeText(this, "storedVerificationId otp: ${storedVerificationId}", Toast.LENGTH_SHORT).show()

        btnVerify.setOnClickListener {
            if (etOtp.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyUserMobile()
            }
        }
    }

    private fun saveUserData(name: String, email: String, number: String) {
        Toast.makeText(this@OTPVerification, "User details saved", Toast.LENGTH_SHORT).show()
        val userId = auth.currentUser?.uid
        Toast.makeText(this, "UserId: ${userId}", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", false)
        editor.putString("userName", name)
        editor.apply()
        val user = UserModel(userId, name, email, number, false)
        dbRef.child(userId!!).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this@OTPVerification, "User Registration Successful", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this@OTPVerification, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verifyUserMobile() {
        val otp = etOtp.text.trim().toString()
        Toast.makeText(this@OTPVerification, "Verify user mobile", Toast.LENGTH_SHORT).show()
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val name = intent.getStringExtra("name")!!
        val email = intent.getStringExtra("email")!!
        val number = intent.getStringExtra("number")!!
        saveUserData(name, email, number)
        val intent = Intent(this@OTPVerification, UserDashboard::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}