package com.example.nathanjohannesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        usernameEditText =
            findViewById(R.id.etUsername)

        passwordEditText =
            findViewById(R.id.etPassword)

        loginButton =
            findViewById(R.id.btnLogin)

        createButton =
            findViewById(R.id.btnCreateAccount)

        loginButton.setOnClickListener {

            val username =
                usernameEditText.text.toString()

            val password =
                passwordEditText.text.toString()

            if (dbHelper.checkUser(
                    username,
                    password
                )
            ) {

                startActivity(
                    Intent(
                        this,
                        Inventory::class.java
                    )
                )

            } else {
                Toast.makeText(
                    this,
                    "Invalid Login",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        createButton.setOnClickListener {

            val username =
                usernameEditText.text.toString()

            val password =
                passwordEditText.text.toString()

            val success =
                dbHelper.insertUser(
                    username,
                    password
                )

            if (success) {

                Toast.makeText(
                    this,
                    "Account Created",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "User Already Exists",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}