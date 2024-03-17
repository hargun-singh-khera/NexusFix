package com.example.laptoprepairapp

import android.content.Intent
import android.os.Bundle
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

class MainActivity10 : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var ticketList: ArrayList<RequestModel>
    private lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main10)

//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        toolbar.setTitle("Ticket Management")
//        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        tvLoadingData = findViewById(R.id.tvLoadingData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        ticketList = arrayListOf<RequestModel>()
        getAllTickets()

    }

    private fun getAllTickets() {
        recyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Requests")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()){
                    for (ticketSnap in snapshot.children){
                        val ticket = ticketSnap.getValue(RequestModel::class.java)
                        ticketList.add(ticket!!)
                    }
                    val mAdapter = UserTicketAdapter(this@MainActivity10, R.layout.user_ticket_item, ticketList)
                    recyclerView.adapter = mAdapter

                    recyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
                else {
                    tvLoadingData.text = "No Record Found."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}