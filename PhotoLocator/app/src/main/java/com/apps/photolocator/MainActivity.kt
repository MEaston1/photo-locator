package com.apps.photolocator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.apps.photolocator.photo.PhotoRecyclerActivity
import com.apps.photolocator.registerlogin.RegisterActivity
import com.apps.photolocator.registerlogin.User
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var videoView: VideoView

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.apps.photolocator"
    private val description = "Test Notification"

    lateinit var navHeaderImageView: ImageView
    lateinit var navHeaderUsername:TextView

    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        darkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyUserIsLoggedIn()

        toolbar = findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationButton.setOnClickListener{
            notifications()
        }

        getUser()
        displayVideo()
    }
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_images -> {
                startActivity(Intent(this, PhotoRecyclerActivity::class.java))
            }
            R.id.nav_maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.nav_upload_location -> {
                startActivity(Intent(this, UploadNewLocActivity::class.java))
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_signout -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun displayVideo(){
        videoView = findViewById(R.id.videoView)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.travel_world
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnCompletionListener {
            videoView.start()
        }
    }
    fun notifications(){
        val intent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("CodeAndroid")
                .setContentText("Test Notification")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher))
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setContentTitle("CodeAndroid")
                .setContentText("Test Notification")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    fun getUser(){
        val headerView = navView.getHeaderView(0)
        navHeaderImageView = headerView.findViewById(R.id.navHeaderImageView)
        navHeaderUsername = headerView.findViewById(R.id.navHeaderUsername)
        val currentUser = FirebaseAuth.getInstance().currentUser    // find user id from firebase
        val userUid = currentUser?.uid
        ref = FirebaseDatabase.getInstance().getReference("users/$userUid")
        ref.addValueEventListener(object: ValueEventListener {              //if the calorie data changes update the total value
            override fun onDataChange(snapShot: DataSnapshot) {
                val user = snapShot.getValue(User::class.java)
                Picasso.get().load(user?.profileImageUrl).into(navHeaderImageView)
                navHeaderUsername.text = user?.username
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}