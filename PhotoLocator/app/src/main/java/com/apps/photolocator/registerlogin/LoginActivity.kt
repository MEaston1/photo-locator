package com.apps.photolocator.registerlogin
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apps.photolocator.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.apps.photolocator.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_login)

            login_button_login.setOnClickListener {
                peformLogin()
                    }
            back_to_registration_text_view.setOnClickListener{
                finish()
            }
    }

    private fun peformLogin(){
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
                return
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }
}


