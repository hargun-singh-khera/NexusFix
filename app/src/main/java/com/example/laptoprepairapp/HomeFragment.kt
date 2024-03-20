package com.example.laptoprepairapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    lateinit var tvUserName: TextView
    lateinit var btnTrackReq: Button
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    lateinit var cardViewSupportTicket: CardView
    val fileName = "userType"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvUserName = view.findViewById(R.id.tvUserName)
        btnTrackReq = view.findViewById(R.id.btnTrackReq)


        btnTrackReq.setOnClickListener {
            startActivity(Intent(requireContext(), UserTrackRequests::class.java))
        }

        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        tvUserName.text = "Hi, " + userName.toString()

//        Toast.makeText(requireContext(), "${auth.currentUser?.email}", Toast.LENGTH_SHORT).show()

        cardViewSupportTicket = view.findViewById(R.id.cardViewSupportTicket)
        cardViewSupportTicket.setOnClickListener {
            startActivity(Intent(requireContext(), SupportTicket::class.java))
        }
        return view
    }



}