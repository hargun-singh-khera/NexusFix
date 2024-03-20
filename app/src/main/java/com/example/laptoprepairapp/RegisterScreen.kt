package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterScreen : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etPass: EditText
    lateinit var etConfPass: EditText
    lateinit var btnSignup: Button
    lateinit var tvLogin: TextView
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
//    lateinit var progressBar: ProgressBar
//    lateinit var tvCheckEmailVerification: TextView
    val fileName = "userType"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPass = findViewById(R.id.etPass)
        etConfPass = findViewById(R.id.etConfPass)
        btnSignup = findViewById(R.id.btnSignup)
        tvLogin = findViewById(R.id.tvLogin)
//        progressBar = findViewById(R.id.progressBar)
//        tvCheckEmailVerification = findViewById(R.id.tvCheckEmailVerification)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        sharedPreferences = getSharedPreferences(fileName , Context.MODE_PRIVATE)

        btnSignup.setOnClickListener {
            signUpUser()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
        }
    }
    private fun signUpUser() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val number = etMobileNumber.text.toString()
        val pass = etPass.text.toString()
        val cpass = etConfPass.text.toString()
        if (name.isEmpty() || email.isEmpty() || number.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
            Toast.makeText(this@RegisterScreen, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
        else if (number.length != 10) {
            Toast.makeText(this@RegisterScreen, "Mobile number must be of 10 characters. Please enter a valid mobile number.", Toast.LENGTH_SHORT).show()
            etMobileNumber.text.clear()
        }
        else if (pass != cpass) {
            Toast.makeText(this@RegisterScreen, "Password and Confirm Password doesn't matches", Toast.LENGTH_SHORT).show()
            etPass.text.clear()
            etConfPass.text.clear()
        }
        else {
//            progressBar.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        saveUserData(name, email, number)
                    }?.addOnFailureListener {
                        Toast.makeText(this, "Failed to send email verification", Toast.LENGTH_SHORT).show()
                        }
                }

            }
        }
    }

    private fun saveUserData(name: String, email: String, number: String) {
        Toast.makeText(this@RegisterScreen, "User details saved", Toast.LENGTH_SHORT).show()
        val userId = auth.currentUser?.uid
        Toast.makeText(this, "UserId: ${userId}", Toast.LENGTH_SHORT).show()
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", false)
        editor.putString("userName", name)
        editor.apply()
        val user = UserModel(userId, name, email, number, false)
        dbRef.child(userId!!).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this@RegisterScreen, "User Registration Successful", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        val intent = Intent(this@RegisterScreen, UserDashboard::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@RegisterScreen, "Please verify your email and then continue to login", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterScreen, LoginScreen::class.java)
                        startActivity(intent)
                    }
                }
            }
            etName.text.clear()
            etEmail.text.clear()
            etMobileNumber.text.clear()
            etPass.text.clear()
            etConfPass.text.clear()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this@RegisterScreen, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}