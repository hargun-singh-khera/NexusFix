package com.example.laptoprepairapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


class RateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_rate, container, false)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val btnSendRating = view.findViewById<Button>(R.id.btnSendRating)
        val progressBar =  view.findViewById<ProgressBar>(R.id.progressBar)
        val sendingRatingTextView = view.findViewById<TextView>(R.id.sendingRatingTextView)

        btnSendRating.setOnClickListener {
            sendingRatingTextView.setText("Sending your rating ${ratingBar.rating}/${ratingBar.numStars} ...")
            progressBar.visibility=View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                progressBar.visibility=View.INVISIBLE
                sendingRatingTextView.setText(null)
                val snackbar = Snackbar.make(it, "Rating sent successfully", Snackbar.LENGTH_LONG)
                snackbar.setAction("Ok") {

                }
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.setBackgroundTint(resources.getColor(R.color.blue))
                snackbar.show()
            },2600)
        }

        return view
    }
}