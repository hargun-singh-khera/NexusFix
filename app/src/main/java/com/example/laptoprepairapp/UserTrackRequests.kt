package com.example.laptoprepairapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class UserTrackRequests : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    lateinit var bitmap: Bitmap
    lateinit var storageReference: StorageReference
    private lateinit var ticketBitmapMap: HashMap<String, Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main10)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Track Requests")
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!
        ticketBitmapMap = HashMap()

        ticketList = arrayListOf<RequestModel>()
        getAllTickets()

    }

    private fun getAllTickets() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.GONE
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Requests")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()){
                    for (ticketSnap in snapshot.children){
                        val ticket = ticketSnap.getValue(RequestModel::class.java)
                        getAllTicketImages(userId, ticket?.ticketId!!)
                        ticketList.add(ticket!!)
                    }
                    if (ticketList.isEmpty()) {
                        progressBar.visibility = View.GONE
                        tvLoadingData.visibility = View.VISIBLE
                        tvLoadingData.text = "No Record Found."
                    }

                }
                else {
                    progressBar.visibility = View.GONE
                    tvLoadingData.visibility = View.VISIBLE
                    tvLoadingData.text = "No Record Found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getAllTicketImages(userId: String, ticketId: String) {
        storageReference = FirebaseStorage.getInstance().getReference("Users/${userId}/Tickets/").child(ticketId)
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            Log.d("Bitmap2", bitmap.toString())
            ticketBitmapMap[ticketId] = bitmap
            // Checks if all images are fetched
            if (ticketBitmapMap.size == ticketList.size) {
                // If all images are fetched, update the adapter
                updateAdapterWithBitmap()
            }
        } .addOnFailureListener {
            Toast.makeText(this@UserTrackRequests, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAdapterWithBitmap() {
        val mAdapter = UserTicketAdapter(this@UserTrackRequests, R.layout.user_ticket_item, ticketList, ticketBitmapMap, dbRef, userId)
        recyclerView.adapter = mAdapter

        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        tvLoadingData.visibility = View.GONE
    }
}