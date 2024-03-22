package com.example.laptoprepairapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var etEmail: EditText
    lateinit var btnReset: Button
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        etEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)

        progressDialog = ProgressDialog(this)

        btnReset.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
            else {
                showProgressBar()
                resetUserPassword(email)
            }
        }
    }

    private fun showProgressBar() {
        progressDialog.setMessage("Sending Password Reset Link...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun hideProgressBar() {
        progressDialog.dismiss()
    }

    private fun resetUserPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                // used for delay of activity
                Handler(Looper.getMainLooper()).postDelayed({
                    hideProgressBar()
                    finish()
                },1000)
            } else {
                Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}