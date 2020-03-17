package com.apps.photolocator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.apps.photolocator.models.Location

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    lateinit var nameText: TextView                                //defines variables
    lateinit var countryText: TextView
    lateinit var locationImageView: ImageView

    var long = "10"
    var lat = "10"
    lateinit var ref: DatabaseReference
    lateinit var saveToRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        nameText = findViewById(R.id.nameText)
        countryText = findViewById(R.id.countryText)
        locationImageView = findViewById(R.id.locationImageView)

        ref = FirebaseDatabase.getInstance().getReference("Locations/Eiffel Tower")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapShot: DataSnapshot) {
                if (snapShot!!.exists()) {
                    val location = snapShot.getValue(Location::class.java)
                    nameText.text = location?.name
                    countryText.text = location?.country
                    long = location?.long.toString()
                    lat = location?.lat.toString()
                    Picasso.get().load(location?.locationImageUrl).into(locationImageView)
                    updateMap()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //saveData()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }
    private fun updateMap() {

        val coord = LatLng(long.toDouble(), lat.toDouble())
        mMap.addMarker(MarkerOptions().position(coord).title("Marker at " + nameText.text))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 16.0f))
    }


    private fun saveData(){
        val saveToRef = FirebaseDatabase.getInstance().getReference("Locations")
        val id = saveToRef.push().key!!
        val name = "Eiffel Tower"
        val country = "France"
        val lat = "2.2945"
        val long = "48.8584"
        val locationImageUrl = ""
        val location = Location(id, name, country, lat, long, locationImageUrl)
        saveToRef.child(name).setValue(location).addOnCompleteListener{
            Toast.makeText(applicationContext, "Food added successfully", Toast.LENGTH_LONG).show()
        }
    }
}
