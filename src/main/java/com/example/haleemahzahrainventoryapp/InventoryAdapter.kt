package com.example.haleemahzahrainventoryapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class InventoryAdapter(
    private val context: Context,
    private var items: MutableList<Item>,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item_row, parent, false) as LinearLayout
        return ItemViewHolder(layout)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        val tvName = holder.layout.findViewById<TextView>(R.id.tvItemName)
        val tvQuantity = holder.layout.findViewById<TextView>(R.id.tvItemQuantity)
        val spinnerStatus = holder.layout.findViewById<Spinner>(R.id.spinnerStatus)
        val tvExpiration = holder.layout.findViewById<TextView>(R.id.tvItemExpiration) // Added this line

        tvName.text = item.name
        tvQuantity.text = "Qty: ${item.quantity}"
        tvExpiration.text = "Expires: ${item.expirationDate}" 

        // Highlight quantity in red if quantity < 5
        if (item.quantity < 5) {
            tvQuantity.setTextColor(Color.RED)
        } else {
            tvQuantity.setTextColor(Color.BLACK) // reset color if not low
        }

        val statusOptions = arrayOf("In-Stock", "Out of Stock")
        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = spinnerAdapter
        spinnerStatus.setSelection(if (item.status == "In-Stock") 0 else 1)

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                val newStatus = if (pos == 0) "In-Stock" else "Out of Stock"
                if (item.status != newStatus) {
                    item.status = newStatus
                    dbHelper.updateInventoryItem(item.name, item)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        holder.layout.setOnClickListener {
            showEditItemDialog(item, position)
        }
    }

    /**
     * Updates the adapter's list after sorting by expiration date
     */
    fun updateList(newList: List<Item>) {
        // Sort by expiration date before setting the list
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        items = newList.sortedWith(compareBy { item ->
            // Parse date string to Date, handle parse exceptions
            try {
                sdf.parse(item.expirationDate)
            } catch (e: Exception) {
                Date(Long.MAX_VALUE) // put invalid dates at the end
            }
        }).toMutableList()

        notifyDataSetChanged()
    }

    private fun showEditItemDialog(item: Item, position: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_item_details, null)
        val dialog = AlertDialog.Builder(context).setView(view).create()

        val etName = view.findViewById<EditText>(R.id.etItemName)
        val etDesc = view.findViewById<EditText>(R.id.etItemDescription)
        val etQuantity = view.findViewById<EditText>(R.id.etQuantity)
        val btnIncrease = view.findViewById<Button>(R.id.btnIncrease)
        val btnDecrease = view.findViewById<Button>(R.id.btnDecrease)
        val btnDelete = view.findViewById<Button>(R.id.btnDeleteItem)
        val btnCancel = view.findViewById<Button>(R.id.btnCancelEdit)
        val btnSave = view.findViewById<Button>(R.id.btnSaveChanges)

        val originalName = item.name

        etName.setText(item.name)
        etDesc.setText(item.description)
        etQuantity.setText(item.quantity.toString())

        btnIncrease.setOnClickListener {
            val q = etQuantity.text.toString().toIntOrNull() ?: 0
            etQuantity.setText((q + 1).toString())
        }

        btnDecrease.setOnClickListener {
            val q = etQuantity.text.toString().toIntOrNull() ?: 0
            if (q > 0) etQuantity.setText((q - 1).toString())
        }

        btnDelete.setOnClickListener {
            items.removeAt(position)
            dbHelper.deleteInventoryItem(originalName)
            notifyDataSetChanged()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newDesc = etDesc.text.toString().trim()
            val newQty = etQuantity.text.toString().toIntOrNull()

            if (newName.isNotBlank() && newDesc.isNotBlank() && newQty != null) {
                val updatedItem = item.copy(name = newName, description = newDesc, quantity = newQty)
                items[position] = updatedItem
                dbHelper.updateInventoryItem(originalName, updatedItem)
                notifyItemChanged(position)

                // Send SMS alert if quantity is 0 and permission is granted
                if (newQty == 0 &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                ) {
                    SmsUtil.sendLowInventoryAlert(context, newName)
                }

                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
