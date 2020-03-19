package com.apps.photolocator

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.apps.photolocator.models.Location
import com.apps.photolocator.registerlogin.RegisterActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.returnText
import kotlinx.android.synthetic.main.activity_upload_new_loc.*
import java.util.*


class UploadNewLocActivity : AppCompatActivity() {

    lateinit var addImageView: ImageView
    lateinit var nameText: TextView
    lateinit var countryText: TextView
    lateinit var latText: TextView
    lateinit var longText: TextView
    lateinit var descriptionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_new_loc)

        addImageView = findViewById(R.id.addImageView)
        nameText = findViewById(R.id.nameText)
        countryText = findViewById(R.id.countryText)
        latText = findViewById(R.id.latText)
        longText = findViewById(R.id.longText)
        descriptionText = findViewById(R.id.descriptionText)

        returnText.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        uploadButton.setOnClickListener{
            
        }

        addImageView.setOnClickListener{
            selectImage(this)
        }
    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                //dispatchTakePictureIntent()
            } else if (options[item] == "Choose from Gallery") {
                pickImageFromGallery()

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    var selectedPhotoUri: Uri? = null

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d(RegisterActivity.TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            addImageView.setImageBitmap(bitmap)

            addImageView.alpha = 0f
            //        val bitmapDrawable = BitmapDrawable(bitmap)
            //        select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }*/

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            addImageView.setImageURI(data?.data)
            selectedPhotoUri = data?.data
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref  = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(RegisterActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    saveData(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(RegisterActivity.TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveData(locationImageUrl: String){
        val saveToRef = FirebaseDatabase.getInstance().getReference("Locations")
        val id = saveToRef.push().key!!
        val name = nameText.text.toString().trim()
        val country = countryText.text.toString().trim()
        val lat = latText.text.toString().trim()
        val long = longText.text.toString().trim()
        val description = descriptionText.text.toString().trim()
        val location = Location(id, name, country, lat, long, locationImageUrl, description)
        saveToRef.child(name).setValue(location).addOnCompleteListener{
            Toast.makeText(applicationContext, "Location added successfully", Toast.LENGTH_LONG).show()
        }
    }
}

