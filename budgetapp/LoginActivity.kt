package com.example.budgetapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import models.User

class LoginActivity : AppCompatActivity() {

    //Declare them here so all methods in the class can access them
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var db: BudgetDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Now assign them inside onCreate after the layout is loaded
         username = findViewById<EditText>(R.id.etLoginUsername)
         password = findViewById<EditText>(R.id.etLoginPassword)
         db = BudgetDatabase.getDatabase(this)

    }



    fun login(view: View) {

        val name = username.text.toString().trim()
        val pwd = password.text.toString().trim()

        if (name.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_LONG).show()
            return
        }

        Thread {
            val user: User? = db.userDao().getUserByUsername(name)
            runOnUiThread {
                if (user == null || user.password != pwd) {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()

                    // Pass userId to next activity via intent
                    val intent = Intent(this, ExpenseActivity::class.java)
                    intent.putExtra("USER_ID", user.id)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()


    }


}