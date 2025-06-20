package com.example.haleemahzahrainventoryapp.controller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haleemahzahrainventoryapp.controller.InventoryController
import com.example.haleemahzahrainventoryapp.model.Item

class InventoryActivity : AppCompatActivity() {

    private lateinit var controller: InventoryController
    private lateinit var adapter: InventoryAdapter
    private lateinit var itemList: MutableList<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val dbHelper = DatabaseHelper(this)
        controller = InventoryController(this, dbHelper)

        itemList = controller.getAllItems()
        adapter = InventoryAdapter(this, itemList, controller)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null)
        val etItemName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerType)

        AlertDialog.Builder(this)
            .setTitle("Add New Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etItemName.text.toString()
                val quantity = etQuantity.text.toString().toIntOrNull() ?: 0
                val color = spinnerColor.selectedItem.toString()
                val type = spinnerType.selectedItem.toString()
                val description = etDescription.text.toString()

                val item = Item(name, quantity, color, type, description)

                if (controller.addItem(item)) {
                    itemList.add(item)
                    adapter.notifyItemInserted(itemList.size - 1)
                } else {
                    Toast.makeText(this, "Item already exists", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
