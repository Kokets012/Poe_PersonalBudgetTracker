package com.example.budgetapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import models.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user : User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    fun login(username: String, password: String) : User? // nullable User

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): User?

}