package com.apps.photolocator


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.apps.photolocator.models.Location
import com.apps.photolocator.photo.PhotoRecyclerActivity
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

    lateinit var navView: NavigationView
    lateinit var toolbar: Toolbar

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

        val location = intent.getParcelableExtra<Location>(PhotoRecyclerActivity.PHOTO_KEY)

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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (useDarkMode){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_dark_mode))
        }

    }
    private fun updateMap() {

        val coord = LatLng(long.toDouble(), lat.toDouble())

        mMap.addMarker(MarkerOptions().position(coord).title("Marker at " + nameText.text))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 16.0f))
    }


    private fun saveData(){
        val saveToRef = FirebaseDatabase.getInstance().getReference("Locations")
        val id = saveToRef.push().key!!
        val name = "Stonehenge"
        val country = "England"
        val lat = "1.8262"
        val long = "51.1789"
        val locationImageUrl = "data:image/jpeg;base64,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"
        val description = "stones"
        val location = Location(id, name, country, lat, long, locationImageUrl, description)
        saveToRef.child(name).setValue(location).addOnCompleteListener{
            Toast.makeText(applicationContext, "Food added successfully", Toast.LENGTH_LONG).show()
        }
    }
}