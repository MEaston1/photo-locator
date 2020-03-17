package com.apps.photolocator.models

class Location (val id: String, val name: String, val country: String, val lat: String, val long: String, val locationImageUrl: String) {

    constructor(): this ("", "", "", "", "",""){
    }
}