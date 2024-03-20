package com.example.laptoprepairapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var editProfileButton: Button
    private lateinit var myLocation: TextView
    private lateinit var logoutButton: Button
    lateinit var progressBar: ProgressBar
    lateinit var layout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences

    private var inEditMode = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    lateinit var auth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var userId: String
    lateinit var userEmail: String
    lateinit var userName: String
    lateinit var userMobileNumber: String

    val fileName = "userType"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        nameEditText = view.findViewById(R.id.nameEditText)
        mobileEditText = view.findViewById(R.id.mobileEditText)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        myLocation = view.findViewById(R.id.myLocation)
        logoutButton = view.findViewById(R.id.logoutButton)
        progressBar = view.findViewById(R.id.progressBar)
//        layout = view.findViewById(R.id.layout)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid!!

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE)

        getAllUserData()

        editProfileButton.setOnClickListener {
            if (inEditMode) {
                inEditMode = false
                editProfileButton.text = "Edit Profile"

                nameEditText.isEnabled = false
                emailEditText.isEnabled = false
                mobileEditText.isEnabled = false

                nameEditText.isFocusable = true
                emailEditText.isFocusable = true
                mobileEditText.isFocusable = true

                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val mobile = mobileEditText.text.toString()
                updateUserDetails(name, email, mobile)
            } else {
                inEditMode = true
                editProfileButton.text = "Save Profile"

                nameEditText.isEnabled = true
                emailEditText.isEnabled = true
                mobileEditText.isEnabled = true

                nameEditText.isFocusable = true
                emailEditText.isFocusable = true
                mobileEditText.isFocusable = true

                nameEditText.isFocusableInTouchMode = true
                emailEditText.isFocusableInTouchMode = true
                mobileEditText.isFocusableInTouchMode = true
            }
        }

        logoutButton.setOnClickListener {
            exitAlert()
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        getLocation() // Call this method to get the location


        return view
    }

    private fun updateUserDetails(name: String, email: String, mobile: String) {
        val edit = sharedPreferences.edit()
        edit.putString("userName", name)
        edit.apply()

        val user = UserModel(userId, name, email, mobile, false)
        dbRef.child(userId).setValue(user).addOnSuccessListener {
            Toast.makeText(requireContext(), "User details updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error while uploading your details", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllUserData() {
//        layout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        dbRef.child(userId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val user = snapshot.getValue(UserModel::class.java)
                        userName = user?.userName!!
                        userEmail = user?.userEmail!!
                        userMobileNumber = user?.userMobileNumber!!

                        nameEditText.setText(userName)
                        emailEditText.setText(userEmail)
                        mobileEditText.setText(userMobileNumber)

                        progressBar.visibility = View.GONE
//                        layout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "An error occurred " + error.message , Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun exitAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to logout ?")
        builder.setTitle("Logout Alert!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> logoutRedirect()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun logoutRedirect() {
        val intent = Intent(requireActivity(), LoginScreen::class.java)
        startActivity(intent)
    }

    private fun getLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address>?
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (list != null) {
                            if (list.isNotEmpty()) {
                                myLocation.text = "${list[0].getAddressLine(0)}"
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), permissionId)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }


}