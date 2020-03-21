package com.apps.photolocator.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Location (val id: String, val name: String, val country: String, val lat: String, val long: String, val locationImageUrl: String, val description: String) :
    Parcelable {

    constructor(): this ("", "", "", "", "","",""){
    }
}



