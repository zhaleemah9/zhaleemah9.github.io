package com.example.haleemahzahrainventoryapp.controller

import android.content.Context
import com.example.haleemahzahrainventoryapp.DatabaseHelper
import com.example.haleemahzahrainventoryapp.model.Item
import com.example.haleemahzahrainventoryapp.util.SmsUtil

class InventoryController(private val context: Context, private val dbHelper: DatabaseHelper) {

    fun updateItem(originalName: String, updatedItem: Item): Boolean {
        updatedItem.status = if (updatedItem.quantity == 0) "Out of Stock" else "In Stock"
        val success = dbHelper.updateInventoryItem(originalName, updatedItem)
        if (success && updatedItem.quantity == 0) {
            SmsUtil.sendLowInventoryAlert(context, updatedItem.name)
        }
        return success
    }

    fun deleteItem(itemName: String): Boolean {
        return dbHelper.deleteInventoryItem(itemName)
    }

    fun getAllItems(): MutableList<Item> {
        return dbHelper.getAllInventoryItems()
    }

    fun addItem(item: Item): Boolean {
        item.status = if (item.quantity == 0) "Out of Stock" else "In Stock"
        return dbHelper.insertInventoryItem(item)
    }
}
