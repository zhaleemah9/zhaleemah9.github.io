package com.example.haleemahzahrainventoryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    private lateinit var inventoryAdapter: InventoryAdapter
    private val itemList = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val recyclerView = findViewById<RecyclerView>(R.id.inventoryRecyclerView)
        val btnAddItem = findViewById<Button>(R.id.btnAddItem)

        inventoryAdapter = InventoryAdapter(this, itemList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = inventoryAdapter

        btnAddItem.setOnClickListener {
            showAddItemDialog()
        }

        loadInventoryFromFirebase()
    }

    private fun loadInventoryFromFirebase() {
        FirebaseUtil.getItems { items ->
            itemList.clear()
            itemList.addAll(items)
            inventoryAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddItemDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()

        val etName = view.findViewById<EditText>(R.id.etNewItemName)
        val etDesc = view.findViewById<EditText>(R.id.etNewItemDescription)
        val etQty = view.findViewById<EditText>(R.id.etNewItemQuantity)
        val btnBack = view.findViewById<Button>(R.id.btnBackFromAddItem)
        val btnSave = view.findViewById<Button>(R.id.btnSaveItem)

        btnBack.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val qty = etQty.text.toString().toIntOrNull()

            if (name.isNotBlank() && desc.isNotBlank() && qty != null) {
                val newItem = Item(name, desc, qty)
                FirebaseUtil.addItem(newItem)
                itemList.add(newItem)
                inventoryAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
