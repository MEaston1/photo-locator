package com.apps.photolocator


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.apps.photolocator.models.Location
import com.apps.photolocator.photo.PhotoRecyclerActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    lateinit var nameText: TextView                                //defines variables
    lateinit var countryText: TextView
    lateinit var locationImageView: ImageView
    lateinit var descriptionText: TextView

    var long = "10"
    var lat = "10"
    lateinit var ref: DatabaseReference

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val location = intent.getParcelableExtra<Location>(PhotoRecyclerActivity.PHOTO_KEY)                             // fetches location class
        val placeName = location.name
        val ref = location.locationImageUrl
        shareToOtherAppsButton.setOnClickListener{
            val shareIntent = Intent()                                                                                  // new intent
            shareIntent.action = Intent.ACTION_SEND                                                                     //
            shareIntent.type = "text/plain*"                                                                                // self explanatory
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, placeName)
            shareIntent.putExtra(Intent.EXTRA_TEXT, ref)                                                                // adds location name to sharing
            startActivity(Intent.createChooser(shareIntent, "Share This Image"))
        }

        //These two functions below are my backup attempts

        //  shareToOtherAppsButton.setOnClickListener{
            //     Picasso.get().load(location?.locationImageUrl).into(locationImageView)
            //      fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                //           val shareIntent = Intent()
                //          Uri path = Uri.parse()
                //          shareIntent.action = Intent.ACTION_SEND
                //          shareIntent.type = "image/*"
                //          shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap?))
                //      }
            //   }

    }


    // shareToOtherAppsButton.setOnClickListener{
        //     Picasso.get().load(location?.locationImageUrl).into(locationImageView)
        //     {
            //         fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                //             val i = Intent(Intent.ACTION_SEND)
                //            val shareIntent = Intent()
                //             shareIntent.type  = "image/*"
                //            i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap))
                //             ContextCompat.startActivity(Intent.createChooser(i, "Share Image"))
                //        }

            //        fun onBitmapFailed(errorDrawable: Drawable?) {}
     //       fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
     //   })
     // }
    fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
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

}