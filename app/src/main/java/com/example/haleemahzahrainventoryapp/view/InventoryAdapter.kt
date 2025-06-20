package com.example.haleemahzahrainventoryapp.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.haleemahzahrainventoryapp.controller.InventoryController
import com.example.haleemahzahrainventoryapp.model.Item
import com.example.haleemahzahrainventoryapp.util.SmsUtil

class InventoryAdapter(
    private val context: Context,
    private val items: MutableList<Item>,
    private val controller: InventoryController
) : RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val tvColor: TextView = view.findViewById(R.id.tvColor)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvQuantity.text = "Quantity: ${item.quantity}"
        holder.tvColor.text = "Color: ${item.color}"
        holder.tvType.text = "Type: ${item.type}"
        holder.tvDescription.text = "Description: ${item.description}"

        holder.btnEdit.setOnClickListener {
            showEditDialog(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun showEditDialog(item: Item, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_item_details, null)
        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val spinnerColor = dialogView.findViewById<Spinner>(R.id.spinnerColor)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerType)

        etName.setText(item.name)
        etQuantity.setText(item.quantity.toString())
        etDescription.setText(item.description)
        spinnerColor.setSelection((spinnerColor.adapter as ArrayAdapter<String>).getPosition(item.color))
        spinnerType.setSelection((spinnerType.adapter as ArrayAdapter<String>).getPosition(item.type))

        AlertDialog.Builder(context)
            .setTitle("Edit Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val originalName = item.name
                val newName = etName.text.toString()
                val newQty = etQuantity.text.toString().toIntOrNull() ?: 0
                val newColor = spinnerColor.selectedItem.toString()
                val newType = spinnerType.selectedItem.toString()
                val newDescription = etDescription.text.toString()

                val updatedItem = Item(newName, newQty, newColor, newType, newDescription)

                if (controller.updateItem(originalName, updatedItem)) {
                    items[position] = updatedItem
                    notifyItemChanged(position)

                    if (newQty == 0) {
                        val smsSent = SmsUtil.sendLowInventoryAlert(context, newName)
                        val smsMessage = if (smsSent) {
                            "Low inventory SMS alert sent."
                        } else {
                            "Failed to send SMS alert."
                        }
                        Toast.makeText(context, smsMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Delete") { _, _ ->
                if (controller.deleteItem(item.name)) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
}
