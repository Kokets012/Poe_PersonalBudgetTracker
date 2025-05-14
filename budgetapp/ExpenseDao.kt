package com.example.budgetapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import models.Expense

@Dao
interface ExpenseDao {

    @Insert
    fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE userOwnerId = :userId")
    fun getExpensesByUser(userId: Int): List<Expense>


}