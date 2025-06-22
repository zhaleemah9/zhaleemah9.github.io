package com.example.haleemahzahrainventoryapp

data class Item(
    var name: String,
    var description: String,
    var quantity: Int,
    var status: String = "In-Stock"
)
