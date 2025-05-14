package com.example.budgetapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var conFirmPass: EditText
    private lateinit var db: BudgetDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

         username = findViewById<EditText>(R.id.etUsername)
         email = findViewById<EditText>(R.id.etEmail)
         password = findViewById<EditText>(R.id.etPassword)
         conFirmPass = findViewById<EditText>(R.id.etConPassword)
         db = BudgetDatabase.getDatabase(this)
    }




    fun register(view : View){

        val name = username.text.toString()
        val mail = email.text.toString()
        val pwd = password.text.toString()
        val confirmPwd = conFirmPass.text.toString()

        if (name.isEmpty() || mail.isEmpty() || pwd.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
            return
        }

        if (pwd != confirmPwd) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        Thread {
            val existing = db.userDao().getUserByUsername(name)
            runOnUiThread {
                if (existing != null) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show()
                } else {
                    //allows code to run in the background not filtering user input
                    Thread {
                        db.userDao().insertUser(
                            User(username = name, email = mail, password = pwd, conPassword = confirmPwd)
                        )
                        runOnUiThread {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }.start()
                }
            }
        }.start()

        /*Thread{ //allows code to run in the background not filtering user input
            val existing = db.userDao().getUserByUsername(name)
            if (existing == null){
                db.userDao().insertUser(User(username = name, email = mail, password = pwd, conPassword = confirmPwd))
            }
        }.start()

        Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Register Clicked", Toast.LENGTH_SHORT).show()
        finish()*/

    }

}