package com.example.googleeatkot.Model

import android.provider.Contacts

class Results {
//    public PlusCode plus_code { get; set; }
//    public int user_ratings_total { get; set; }

    var name: String? = null
    var icon: String? = null
    var geometry: Geometry? = null
    var photos: Array<Photos>? = null
    var id: String? = null
    var place_id: String? = null
    var price_level: Int = 0
    var rating: Double = 0.0
    var reference: String? = null
    var scope: String? = null
    var types: Array<String>? = null
    var vicinity: String? = null
    var opening_hours: OpeningHours? = null

    var address_components : Array<AddressComponent>? = null
    var adr_address : String? = null
    var formatted_address : String? = null
    var formatted_phone_number : String? = null
    var international_phone_number : String? = null
    var reviews : Array<Review>? = null
    var url : String? = null
    var user_ratings_total : Int = 0
    var utc_offset : Int = 0
    var website : String? = null
//    var plus_code : Plus_code,



}