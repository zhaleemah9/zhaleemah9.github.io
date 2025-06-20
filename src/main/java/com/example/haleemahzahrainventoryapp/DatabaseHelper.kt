package com.example.haleemahzahrainventoryapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "InventoryApp.db"
        private const val DATABASE_VERSION = 3 

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Inventory table
        private const val TABLE_INVENTORY = "inventory_items"
        private const val COLUMN_ITEM_ID = "item_id"
        private const val COLUMN_ITEM_NAME = "item_name"
        private const val COLUMN_ITEM_DESC = "item_description"
        private const val COLUMN_ITEM_QUANTITY = "item_quantity"
        private const val COLUMN_ITEM_STATUS = "item_status"
        private const val COLUMN_EXPIRATION_DATE = "expiration_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        val createInventoryTable = """
            CREATE TABLE $TABLE_INVENTORY (
                $COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ITEM_NAME TEXT,
                $COLUMN_ITEM_DESC TEXT,
                $COLUMN_ITEM_QUANTITY INTEGER,
                $COLUMN_ITEM_STATUS TEXT,
                $COLUMN_EXPIRATION_DATE TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createInventoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE $TABLE_INVENTORY ADD COLUMN $COLUMN_EXPIRATION_DATE TEXT")
        }
    }

    // User Functions
    fun insertUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        return try {
            db.insertOrThrow(TABLE_USERS, null, values)
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = """
            SELECT * FROM $TABLE_USERS 
            WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?
        """
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val result = cursor.count > 0
        cursor.close()
        db.close()
        return result
    }

    // Inventory Functions
    fun insertInventoryItem(item: Item): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, item.name)
            put(COLUMN_ITEM_DESC, item.description)
            put(COLUMN_ITEM_QUANTITY, item.quantity)
            put(COLUMN_ITEM_STATUS, item.status)
            put(COLUMN_EXPIRATION_DATE, item.expirationDate)
        }

        val result = db.insert(TABLE_INVENTORY, null, values)
        db.close()
        return result != -1L
    }

    fun getAllInventoryItems(): MutableList<Item> {
        val itemList = mutableListOf<Item>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_INVENTORY"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESC))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_STATUS))
                val expirationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRATION_DATE))
                itemList.add(Item(name, desc, quantity, status, expirationDate))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return itemList
    }

    fun updateInventoryItem(oldName: String, updatedItem: Item): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, updatedItem.name)
            put(COLUMN_ITEM_DESC, updatedItem.description)
            put(COLUMN_ITEM_QUANTITY, updatedItem.quantity)
            put(COLUMN_ITEM_STATUS, updatedItem.status)
            put(COLUMN_EXPIRATION_DATE, updatedItem.expirationDate)
        }

        val result = db.update(
            TABLE_INVENTORY,
            values,
            "$COLUMN_ITEM_NAME = ?",
            arrayOf(oldName)
        )

        db.close()
        return result > 0
    }

    fun deleteInventoryItem(name: String): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_INVENTORY, "$COLUMN_ITEM_NAME = ?", arrayOf(name))
        db.close()
        return result > 0
    }
}
