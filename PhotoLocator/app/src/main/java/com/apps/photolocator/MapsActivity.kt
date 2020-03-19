package com.apps.photolocator


import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.apps.photolocator.models.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    lateinit var nameText: TextView                                //defines variables
    lateinit var countryText: TextView
    lateinit var locationImageView: ImageView
    lateinit var descriptionText: TextView

    var long = "10"
    var lat = "10"
    lateinit var ref: DatabaseReference
    lateinit var saveToRef: DatabaseReference

    var useDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        useDarkMode = darkMode()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        returnText.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        nameText = findViewById(R.id.nameText)
        countryText = findViewById(R.id.countryText)
        locationImageView = findViewById(R.id.locationImageView)
        descriptionText = findViewById(R.id.descriptionText)

        descriptionText.setMovementMethod(ScrollingMovementMethod())

        ref = FirebaseDatabase.getInstance().getReference("Locations/Eiffel Tower")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapShot: DataSnapshot) {
                if (snapShot!!.exists()) {
                    val location = snapShot.getValue(Location::class.java)
                    nameText.text = location?.name
                    countryText.text = location?.country
                    long = location?.long.toString()
                    lat = location?.lat.toString()
                    descriptionText.text = location?.description
                    Picasso.get().load(location?.locationImageUrl).into(locationImageView)
                    updateMap()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
        saveData()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (useDarkMode){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_dark_mode))
        }

    }
    private fun updateMap() {

        val coord = LatLng(long.toDouble(), lat.toDouble())

        mMap.addMarker(MarkerOptions().position(coord).title("Marker at " + nameText.text))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 5.0f))
    }

    private fun saveData(){
        val saveToRef = FirebaseDatabase.getInstance().getReference("Locations")
        val id = saveToRef.push().key!!
        val name = "8"
        val country = "France"
        val lat = "2.2945"
        val long = "48.8584"
        val locationImageUrl = ""
        val description = ""
        val location = Location(id, name, country, lat, long, locationImageUrl, description)
        saveToRef.child(name).setValue(location).addOnCompleteListener{
            Toast.makeText(applicationContext, "Food added successfully", Toast.LENGTH_LONG).show()
        }
    }
}