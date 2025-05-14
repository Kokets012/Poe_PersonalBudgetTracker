package models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = User::class, //gets the primary key in Users class
            parentColumns = ["id"],
            childColumns = ["userOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val userOwnerId: Int,
    val name: String,
    val amount: Double,
    val description: String,
    val date: String,
    val category: String,
    val image: ByteArray? // <-- NEW: to store the photo
)
