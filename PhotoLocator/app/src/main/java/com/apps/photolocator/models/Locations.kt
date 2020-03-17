package com.apps.photolocator.models

class Locations (val id: String, val locationName: String, val lat: String, val long: String, val locationImageUrl: String) {

    constructor(): this ("", "", "", "", ""){
    }
}