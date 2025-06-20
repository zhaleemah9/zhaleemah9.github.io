package com.example.haleemahzahrainventoryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var inventoryAdapter: InventoryAdapter
    private val itemList = mutableListOf<Item>()

    private lateinit var spinnerFilterStatus: Spinner
    private lateinit var btnSortExpiration: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        dbHelper = DatabaseHelper(this)
        itemList.addAll(dbHelper.getAllInventoryItems())

        val recyclerView = findViewById<RecyclerView>(R.id.inventoryRecyclerView)
        val btnAddItem = findViewById<Button>(R.id.btnAddItem)

        // Initialize the filter spinner and sort button from layout
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus)
        btnSortExpiration = findViewById(R.id.btnSortExpiration)

        // Setup adapter and RecyclerView
        inventoryAdapter = InventoryAdapter(this, itemList, dbHelper)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = inventoryAdapter

        // Setup status filter spinner
        val filterOptions = arrayOf("All", "In-Stock", "Out of Stock")
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterStatus.adapter = filterAdapter

        spinnerFilterStatus.setSelection(0) // default to "All"

        spinnerFilterStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                filterAndSortItems()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        // Sort button toggles sorting by expiration date ascending
        btnSortExpiration.setOnClickListener {
            filterAndSortItems()
        }

        btnAddItem.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun filterAndSortItems() {
        val selectedStatus = spinnerFilterStatus.selectedItem as String

        // Filter items based on status
        val filtered = if (selectedStatus == "All") {
            itemList
        } else {
            itemList.filter { it.status == selectedStatus }
        }

        // Call adapter updateList to sort by expiration date internally
        inventoryAdapter.updateList(filtered)
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
            val name = etName.text.toString()
            val desc = etDesc.text.toString()
            val qty = etQty.text.toString().toIntOrNull()

            if (name.isNotBlank() && desc.isNotBlank() && qty != null) {
                val newItem = Item(name, desc, qty)
                itemList.add(newItem)
                dbHelper.insertInventoryItem(newItem)
                // Refresh list with current filter/sort applied
                filterAndSortItems()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
