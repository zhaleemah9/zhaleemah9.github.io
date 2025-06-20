package com.example.haleemahzahrainventoryapp.model

data class Item(
    var name: String,
    var quantity: Int,
    var color: String,
    var type: String,
    var description: String,
    var status: String = if (quantity == 0) "Out of Stock" else "In Stock"
)
