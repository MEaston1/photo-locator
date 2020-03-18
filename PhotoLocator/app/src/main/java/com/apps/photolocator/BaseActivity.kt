package com.apps.photolocator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

//Base Activity allows me to call functions in each activity
//All Activitys class' inherit BaseActivity
abstract class BaseActivity : AppCompatActivity() {

    private val PREFS_NAME = "prefs"                        //Constants for sharedPreference
    private val PREF_DARK_THEME = "dark_theme"

    fun darkMode() : Boolean{
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)            //sets up a sharedPreference
        val useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false)                   //sharedPreference's are used to store information between activitys...
        //also saves when app is fully closed for next use
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark)                                                 //if darkThemeSwitch = True, it sets the theme to Dark Theme
        }
        return useDarkTheme                                                                 //the apps default theme is the light theme that is set in the manifest
    }                                                                                       //the themes colors are defined in the styles.xml file

}