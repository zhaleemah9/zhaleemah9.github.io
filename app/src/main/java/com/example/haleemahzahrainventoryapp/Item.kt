package com.example.haleemahzahrainventoryapp

data class Item(
    var id: String? = null,
    var name: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var status: String = "In-Stock"
)