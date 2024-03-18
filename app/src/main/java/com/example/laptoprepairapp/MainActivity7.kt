package com.example.laptoprepairapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class MainActivity7 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var storageReference: StorageReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    lateinit var ticketId: String
    lateinit var laptopModel: String
    lateinit var problemDesc: String
    lateinit var bitmap: Bitmap
    private lateinit var ticketBitmapMap: HashMap<String, Bitmap>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Ticket Management")
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        ticketBitmapMap = HashMap()


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()

        ticketList = arrayListOf<RequestModel>()
        getAllTickets()

    }

    private fun getAllTickets() {
        recyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()){
                    for (userSnap in snapshot.children){
                        val userTicketRef = userSnap.child("Requests")
                        for (ticketSnap in userTicketRef.children) {
                            val ticket = ticketSnap.getValue(RequestModel::class.java)
                            val reqCompleted = ticket!!.reqCompleted
                            if (reqCompleted == false) {
                                getAllTicketImages(userSnap.key!!, ticket.ticketId!!)
                                ticketList.add(ticket!!)
                            }
                        }
                    }
                    if (ticketList.isEmpty()) {
                        tvLoadingData.text = "No Record Found."
                    }
                }
                else {
                    tvLoadingData.text = "No Record Found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity7, "Failed to load tickets.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getAllTicketImages(userId: String, ticketId: String) {
        storageReference = FirebaseStorage.getInstance().getReference("Users/${userId}/Tickets/").child(ticketId)
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            ticketBitmapMap[ticketId] = bitmap
            // Checks if all images are fetched
            if (ticketBitmapMap.size == ticketList.size) {
                // If all images are fetched, update the adapter
                updateAdapterWithBitmap()
            }
        } .addOnFailureListener {
            Toast.makeText(this@MainActivity7, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAdapterWithBitmap() {
        val mAdapter = TicketAdapter(this@MainActivity7, R.layout.ticket_item, ticketList, ticketBitmapMap)
        recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : TicketAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity7, MainActivity9::class.java)
                userId = ticketList[position].userId!!
                ticketId = ticketList[position].ticketId!!
                laptopModel = ticketList[position].laptopModel!!
                problemDesc = ticketList[position].problemDesc!!
                bitmap = ticketBitmapMap[ticketId]!!
                Log.d("Bitmap", bitmap.toString())
                intent.putExtra("userId", userId)
                intent.putExtra("ticketId", ticketId)
                intent.putExtra("laptopModel", laptopModel)
                intent.putExtra("problemDesc", problemDesc)

                // Convert the Bitmap to a byte array
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val byteArray = stream.toByteArray()
////                intent.putExtra("bitmap", byteArray)
//                Log.d("ByteArray", byteArray.toString())
//                Log.d("ByteArraySize", "Size: ${byteArray.size}")
//                intent.putExtra("bitmap", byteArray)
                startActivity(intent)
            }
        })

        recyclerView.visibility = View.VISIBLE
        tvLoadingData.visibility = View.GONE
    }
}