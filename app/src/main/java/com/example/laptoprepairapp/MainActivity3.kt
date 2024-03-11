package com.example.laptoprepairapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity3 : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etPass: EditText
    lateinit var etConfPass: EditText
    lateinit var btnSignup: Button
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPass = findViewById(R.id.etPass)
        etConfPass = findViewById(R.id.etConfPass)
        btnSignup = findViewById(R.id.btnSignup)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        btnSignup.setOnClickListener {
            signUpUser()
        }
    }
    private fun signUpUser() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val number = etMobileNumber.text.toString()
        val pass = etPass.text.toString()
        val cpass = etConfPass.text.toString()
        if (name.isEmpty() || email.isEmpty() || number.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
            Toast.makeText(this@MainActivity3, "All fields are mandatory", Toast.LENGTH_SHORT).show()
        }
        else if (pass != cpass) {
            Toast.makeText(this@MainActivity3, "Password and Confirm Password doesn't matches", Toast.LENGTH_SHORT).show()
        }
        else {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@MainActivity3, "Sign Up Done", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity3, "Sign Up Not Done" + it.exception, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            val userId = dbRef.push().key!!
            val user = UserModel(userId, name, number)
            dbRef.child(userId).setValue(user).addOnCompleteListener {
                Toast.makeText(this@MainActivity3, "User Registration Successful", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etEmail.text.clear()
                etMobileNumber.text.clear()
                etPass.text.clear()
                etConfPass.text.clear()
            }
                .addOnFailureListener {
                    Toast.makeText(this@MainActivity3, "Error ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        startActivity(Intent(this@MainActivity3, MainActivity4::class.java))
    }
}