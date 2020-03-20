package com.apps.photolocator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : BaseActivity() {
    private val PREFS_NAME = "prefs"                //constants for sharedPreferences
    private val PREF_DARK_THEME = "dark_theme"

    override fun onCreate(savedInstanceState: Bundle?) {
        darkMode()                                                                      //calling function from BaseActivity
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)        //sets up a sharedPreference
        val useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false)               //sharedPreference's are used to store information between activitys...
        //also saves when app is fully closed for next use
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)                                      //creates the activity using the settings_activity layout file

        val darkThemeSwitch = findViewById (R.id.darkThemeSwitch) as Switch              //gets the darkThemeSwitch TextView from the layout file
        darkThemeSwitch.setChecked(useDarkTheme);
        darkThemeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->           //on click listener for darkThemeSwitch
            toggleTheme(isChecked)                                                      //calls toggleTheme function passing true or false
        }

        homeText.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))                       //opens MainActivity when clicked
        }

    }

    private fun toggleTheme(darkTheme: Boolean) {
        val editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(PREF_DARK_THEME, darkTheme)
        editor.apply()                                                                  //pushes either true or false onto the sharedPreference
        val intent = intent
        finish()                                                                        //closes the current activity so that the theme can be updated
        startActivity(intent)                                                           //restarts activity
    }

}