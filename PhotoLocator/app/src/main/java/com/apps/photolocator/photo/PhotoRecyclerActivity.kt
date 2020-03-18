package com.apps.photolocator.photo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.apps.photolocator.R
import com.apps.photolocator.models.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_new_photo.*
import kotlinx.android.synthetic.main.user_row_new_photo.view.*

class PhotoRecyclerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_photo)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
             p0.children.forEach {
                 Log.d("NewPatient", it.toString())
                 val user = it.getValue(User::class.java)
                 if (user != null){
                     adapter.add(UserItem(user)
                     )
                 }
             }
                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, PatientProfileActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                }
                recyclerview_newphoto.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_new_photo)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_photo
    }

}

