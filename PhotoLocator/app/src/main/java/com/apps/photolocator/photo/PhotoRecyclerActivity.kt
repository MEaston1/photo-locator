package com.apps.photolocator.photo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.apps.photolocator.MapsActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.apps.photolocator.R
import com.apps.photolocator.models.Location
import com.apps.photolocator.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_new_photo.*
import kotlinx.android.synthetic.main.user_row_new_photo.view.*

class PhotoRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_photo)

        supportActionBar?.title = "Select Image"
        verifyUserIsLoggedIn()
        fetchLocations()
    }
    companion object{
        val PHOTO_KEY = "PHOTO_KEY"
    }
    private fun fetchLocations(){
        val ref = FirebaseDatabase.getInstance().getReference("/Locations")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    Log.d("NewPatient", it.toString())
                    val location = it.getValue(Location::class.java)
                    if (location != null){
                        adapter.add(LocationItem(location)
                        )
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                    val locationItem = item as LocationItem
                    val intent = Intent(view.context, MapsActivity::class.java)
                    intent.putExtra(PHOTO_KEY, locationItem.location)
                    startActivity(intent)
                }
                recyclerview_newphoto.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.nav_signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class LocationItem(val location: Location): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        Picasso.get().load(location.locationImageUrl).into(viewHolder.itemView.imageView_new_photo)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_photo
    }

}