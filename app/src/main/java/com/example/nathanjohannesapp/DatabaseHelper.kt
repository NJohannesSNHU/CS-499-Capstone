package com.example.nathanjohannesapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "InventoryDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT)"
        )

        db.execSQL(
            "CREATE TABLE inventory (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item_name TEXT," +
                    "quantity INTEGER)"
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS inventory")
        onCreate(db)
    }

    fun insertUser(
        username: String,
        password: String
    ): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put("username", username)
        values.put("password", password)

        val result = db.insert(
            "users",
            null,
            values
        )

        return result != -1L
    }

    fun checkUser(
        username: String,
        password: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        val exists = cursor.count > 0
        cursor.close()

        return exists
    }

    fun insertItem(
        itemName: String,
        quantity: Int
    ): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put("item_name", itemName)
        values.put("quantity", quantity)

        val result = db.insert(
            "inventory",
            null,
            values
        )

        return result != -1L
    }

    fun getAllItems(): MutableList<InventoryItem> {

        val list = mutableListOf<InventoryItem>()
        val db = readableDatabase

        val cursor =
            db.rawQuery("SELECT * FROM inventory", null)

        while (cursor.moveToNext()) {
            list.add(
                InventoryItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
                )
            )
        }

        cursor.close()
        return list
    }

    fun deleteItem(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            "inventory",
            "id=?",
            arrayOf(id.toString())
        )
    }

    fun updateItem(
        id: Int,
        quantity: Int
    ): Int {

        val db = writableDatabase
        val values = ContentValues()

        values.put("quantity", quantity)

        return db.update(
            "inventory",
            values,
            "id=?",
            arrayOf(id.toString())
        )
    }

}