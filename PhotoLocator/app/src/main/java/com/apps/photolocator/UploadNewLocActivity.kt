package com.apps.photolocator

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.android.synthetic.main.activity_maps.returnText
import kotlinx.android.synthetic.main.activity_upload_new_loc.*
import java.util.*

class UploadNewLocActivity : BaseActivity() {

    lateinit var addImageView: ImageView
    lateinit var nameText: TextView
    lateinit var countryText: TextView
    lateinit var latText: TextView
    lateinit var longText: TextView
    lateinit var descriptionText: TextView

    var selectedPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        darkMode()
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
            uploadImageToFirebaseStorage()
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
                dispatchTakePictureIntent()
            } else if (options[item] == "Choose from Gallery") {
                pickImageFromGallery()

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }


    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        selectedPhotoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedPhotoUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        private val PERMISSION_CODE = 1001;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            addImageView.setImageURI(data?.data)
            selectedPhotoUri = data?.data
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            addImageView.setImageURI(selectedPhotoUri)
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

        if (name.isEmpty()){                                            // error checking - checks that enterNameText is not empty
            nameText.error = ("Please enter a name")
            return
        }
        if (country.isEmpty()){
            countryText.error = ("Please enter a country")         // error checking - checks that enterCalorieText is not empty
            return
        }
        if (lat.isEmpty()){
            latText.error = ("Please enter a lat")         // error checking - checks that enterCalorieText is not empty
            return
        }
        if (long.isEmpty()){
            longText.error = ("Please enter a long")         // error checking - checks that enterCalorieText is not empty
            return
        }
        if (description.isEmpty()){
            descriptionText.error = ("Please enter a description")         // error checking - checks that enterCalorieText is not empty
            return
        }

        val location = Location(id, name, country, lat, long, locationImageUrl, description)
        saveToRef.child(name).setValue(location).addOnCompleteListener{
            Toast.makeText(applicationContext, "Location added successfully", Toast.LENGTH_LONG).show()
        }
    }
}

