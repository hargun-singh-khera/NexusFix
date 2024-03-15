package com.example.laptoprepairapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity2 : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etPass: EditText
    lateinit var btnSignin: Button
    lateinit var tvRegister: TextView
    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    var isAdmin: Boolean ?= false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        btnSignin = findViewById(R.id.btnSignin)
        tvRegister = findViewById(R.id.tvRegister)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

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
                    dbRef.orderByChild("userEmail").equalTo(email).addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userSnap in snapshot.children) {
                                    Toast.makeText(this@MainActivity2, "Inside of Loop", Toast.LENGTH_SHORT).show()
                                    val user = userSnap.getValue(UserModel::class.java)
                                    isAdmin = user?.userAdmin
                                    if (isAdmin!!) {
                                        Toast.makeText(this@MainActivity2, "Admin User", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@MainActivity2, MainActivity6::class.java))
                                        finish()
                                    }
                                    else {
                                        Toast.makeText(this@MainActivity2, "Normal User", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@MainActivity2, MainActivity4::class.java))
                                        finish()
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                }
                else {
                    Toast.makeText(this@MainActivity2, "Invalid Credentials.", Toast.LENGTH_SHORT).show()
                    etEmail.text.clear()
                    etPass.text.clear()
                }
            }
        }
    }
}
