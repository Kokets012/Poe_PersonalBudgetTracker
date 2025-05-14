package com.example.budgetapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import models.Expense
import models.User

@Database(entities = [User::class, Expense::class], version = 2, exportSchema = false)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao
    abstract fun expenseDao() : ExpenseDao

    companion object {
        private var INSTANCE: BudgetDatabase? = null

        // Migration from version 1 to version 2 (add image column)
        //This is created to make a new database (Alter table happened)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new 'image' column to the 'expenses' table
                database.execSQL("ALTER TABLE expenses ADD COLUMN image BLOB")
            }
        }

        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "budget_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration here
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}