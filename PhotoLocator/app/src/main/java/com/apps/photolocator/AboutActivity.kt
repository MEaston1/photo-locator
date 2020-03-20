package com.apps.photolocator

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_maps.*

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        darkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        homeText.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}