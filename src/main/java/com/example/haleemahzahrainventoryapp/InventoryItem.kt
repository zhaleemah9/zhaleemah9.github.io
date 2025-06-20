package com.example.haleemahzahrainventoryapp

data class InventoryItem(
    val id: Int,
    var name: String,
    var description: String,
    var quantity: Int,
    var status: String = "In-Stock",
    var expirationDate: String
)
