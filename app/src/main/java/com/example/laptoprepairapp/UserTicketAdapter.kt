package com.example.laptoprepairapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserTicketAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>, val ticketBitmapMap: HashMap<String, Bitmap>, var dbRef: DatabaseReference, var userId: String) : RecyclerView.Adapter<UserTicketAdapter.ViewHolder>() {
    lateinit var ticketId: String
    lateinit var laptopModel: String
    lateinit var problemDesc: String
    lateinit var remarks: String
    var reqCompleted: Boolean?= false
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvTicketId = itemView.findViewById<TextView>(R.id.tvTicketId)
        val tvProblemType = itemView.findViewById<TextView>(R.id.tvProblemType)
        val tvLaptopModel = itemView.findViewById<TextView>(R.id.tvLaptopModel)
        val tvRemarks = itemView.findViewById<TextView>(R.id.tvRemarks)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        val cardAssistance = itemView.findViewById<CardView>(R.id.cardAssistance)
        val cardProblemSolved = itemView.findViewById<CardView>(R.id.cardProblemSolved)
        val rgrp = itemView.findViewById<RadioGroup>(R.id.rgrp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val myObj = objects[position]
        holder.tvTicketId.text = "Ticket ID: ${myObj.ticketId}"
        holder.tvProblemType.text = "Problem: ${myObj.problemDesc}"
        holder.tvLaptopModel.text = "Model: ${myObj.laptopModel}"
        holder.imageView.setImageBitmap(ticketBitmapMap[myObj.ticketId])
        holder.tvStatus.setTextColor(Color.RED)
        if (myObj.reqCompleted!!) {
            holder.tvRemarks.visibility = View.VISIBLE
            holder.cardProblemSolved.visibility = View.VISIBLE
            holder.tvRemarks.text = "Remarks: ${myObj.remarks!!}"
            holder.tvStatus.text = "Completed"
            holder.tvStatus.setTextColor(Color.BLUE)
            if (myObj.choiceChoosen!!) {
                if (myObj.problemSolved!!) {
                    holder.cardAssistance.visibility = View.GONE
                    holder.cardProblemSolved.visibility = View.GONE
                }
                else {
                    holder.cardProblemSolved.visibility = View.GONE
                    holder.cardAssistance.visibility = View.VISIBLE
                }
            }
            else {
                holder.rgrp.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.rb1 -> {
                            // Action when "Yes" button is checked
                            getAndUpdateTicket(position, true, true)
                            Toast.makeText(context, "Thanks for your response.", Toast.LENGTH_SHORT).show()
                            holder.cardProblemSolved.visibility = View.GONE
                        }
                        R.id.rb2 -> {
                            // Action when "No" button is checked
                            getAndUpdateTicket(position, false, true)
                            Toast.makeText(context, "Thanks for your response.", Toast.LENGTH_SHORT).show()
                            holder.cardProblemSolved.visibility = View.GONE
                            holder.cardAssistance.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        else {
            holder.tvRemarks.visibility = View.GONE
            holder.cardAssistance.visibility = View.GONE
            holder.cardProblemSolved.visibility = View.GONE
            holder.tvRemarks.text = "Remarks: ${myObj.remarks!!}"
            holder.tvStatus.text = "Pending"
        }
    }


    private fun getAndUpdateTicket(position: Int, problemSolved: Boolean, choiceChosen: Boolean) {
        ticketId = objects[position].ticketId.toString()
        dbRef.child(ticketId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ticketSnap in snapshot.children) {
                        val ticket = snapshot.getValue(RequestModel::class.java)
                        laptopModel = ticket?.laptopModel!!
                        problemDesc = ticket?.problemDesc!!
                        remarks = ticket?.remarks!!
                        reqCompleted = ticket?.reqCompleted!!

                        val request = RequestModel(userId, ticketId, laptopModel, problemDesc, remarks, reqCompleted, problemSolved, choiceChosen)
                        dbRef.child(ticketId).setValue(request).addOnSuccessListener {

                        } .addOnFailureListener {
                            Toast.makeText(context, "Error occurred " + it.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error occurred " + error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}