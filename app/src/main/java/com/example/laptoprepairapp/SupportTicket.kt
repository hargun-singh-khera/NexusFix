package com.example.laptoprepairapp

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SupportTicket : AppCompatActivity() {
    lateinit var etLaptopModel: EditText
    lateinit var etLaptopProblem: EditText
    lateinit var ivImgUpload: ImageView
    lateinit var btnSubmitRequest: Button
    var fileUri: Uri?= null
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var userId: String?= null
    lateinit var ticketId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Support Ticket")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etLaptopModel = findViewById(R.id.etLaptopModel)
        etLaptopProblem = findViewById(R.id.etLaptopProblem)
        ivImgUpload = findViewById(R.id.ivImgUpload)
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId!!).child("Requests")
        ticketId = dbRef.push().key!!

        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    fileUri = it
                }
            })

        ivImgUpload.setOnClickListener {
            Toast.makeText(this, "Upload image clicked", Toast.LENGTH_SHORT).show()
            getImage.launch("image/*")

        }

        btnSubmitRequest.setOnClickListener {
            submitForm()
        }
    }

    private fun uploadImage() {
        if (fileUri != null) {
            Toast.makeText(this@SupportTicket, "FileUri not null", Toast.LENGTH_SHORT).show()
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading....")
            progressDialog.setMessage("Uploading your image...")
            progressDialog.show()
            if (userId != null) {
                Toast.makeText(this@SupportTicket, "Ticket id: ${ticketId}", Toast.LENGTH_SHORT).show()
                val ref: StorageReference = FirebaseStorage.getInstance().getReference("Users/${userId}/Tickets/").child(ticketId)
                ref.putFile(fileUri!!).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@SupportTicket, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@SupportTicket, " " + it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            Toast.makeText(this@SupportTicket, "File uri null found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun submitForm() {
        val laptopModel = etLaptopModel.text.toString()
        val laptopProblemDesc = etLaptopProblem.text.toString()
        if (laptopModel.isEmpty()) {
            Toast.makeText(this@SupportTicket, "Please enter your laptop model.", Toast.LENGTH_SHORT).show()
        }
        else if (laptopProblemDesc.isEmpty()) {
            Toast.makeText(this@SupportTicket, "Please describe your problem.", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "User Id: ${userId}", Toast.LENGTH_SHORT).show()
            val ticket = RequestModel(userId, ticketId, laptopModel, laptopProblemDesc)
            dbRef.child(ticketId).setValue(ticket).addOnCompleteListener {
                if (it.isSuccessful) {
                    uploadImage()
                    Toast.makeText(this@SupportTicket, "Your repair request has been received. Our team will get back to you shortly.", Toast.LENGTH_SHORT).show()
                }
                etLaptopModel.text.clear()
                etLaptopProblem.text.clear()
            }.addOnFailureListener {
                Toast.makeText(this@SupportTicket, "Error ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}