package com.example.laptoprepairapp

import android.content.Context
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

class UserTicketAdapter(val context: Context, val resource: Int, val objects: ArrayList<RequestModel>) : RecyclerView.Adapter<UserTicketAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
//        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val tvTicketId = itemView.findViewById<TextView>(R.id.tvTicketId)
        val tvProblemType = itemView.findViewById<TextView>(R.id.tvProblemType)
        val tvLaptopModel = itemView.findViewById<TextView>(R.id.tvLaptopModel)
        val tvRemarks = itemView.findViewById<TextView>(R.id.tvRemarks)
        val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        val tvProblemSolved = itemView.findViewById<TextView>(R.id.tvProblemSolved)
        val cardAssistance = itemView.findViewById<CardView>(R.id.cardAssistance)
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

        if (myObj.reqCompleted!!) {
            holder.tvRemarks.visibility = View.VISIBLE
            holder.tvProblemSolved.visibility = View.VISIBLE
            holder.rgrp.visibility = View.VISIBLE
            holder.tvRemarks.text = "Remarks: ${myObj.remarks!!}"
            holder.tvStatus.text = "Completed"
            holder.rgrp.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb1 -> {
                        // Action when "Yes" button is checked
                        Toast.makeText(context, "Thanks for your response.", Toast.LENGTH_SHORT).show()
                        holder.tvProblemSolved.visibility = View.GONE
                        holder.rgrp.visibility = View.GONE
                    }
                    R.id.rb2 -> {
                        // Action when "No" button is checked
                        holder.cardAssistance.visibility = View.VISIBLE
                        holder.tvProblemSolved.visibility = View.GONE
                        holder.rgrp.visibility = View.GONE
                    }
                }
            }
        }
        else {
            holder.tvRemarks.visibility = View.GONE
            holder.tvProblemSolved.visibility = View.GONE
            holder.cardAssistance.visibility = View.GONE
            holder.rgrp.visibility = View.GONE
            holder.tvRemarks.text = "Remarks: ${myObj.remarks!!}"
            holder.tvStatus.text = "Pending"
        }

    }
}