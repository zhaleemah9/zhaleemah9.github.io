package com.example.haleemahzahrainventoryapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FirebaseUtil {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun registerUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) callback(true, null)
                else callback(false, it.exception?.message)
            }
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) callback(true, null)
                else callback(false, it.exception?.message)
            }
    }

    fun addItem(item: Item) {
        val itemId = database.child("inventory").push().key ?: return
        item.id = itemId  // set the ID before saving
        database.child("inventory").child(itemId).setValue(item)
    }

    fun getItems(callback: (List<Item>) -> Unit) {
        database.child("inventory").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Item>()
                for (itemSnap in snapshot.children) {
                    val item = itemSnap.getValue(Item::class.java)
                    item?.id = itemSnap.key  // assign the Firebase ID
                    item?.let { list.add(it) }
                }
                callback(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun updateItem(item: Item) {
        val itemId = item.id ?: return
        database.child("inventory").child(itemId).setValue(item)
    }

    fun deleteItem(item: Item) {
        val itemId = item.id ?: return
        database.child("inventory").child(itemId).removeValue()
    }
}
