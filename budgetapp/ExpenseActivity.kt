package com.example.budgetapp

import android.app.DatePickerDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import models.Expense
import java.io.InputStream
import java.util.Calendar

class ExpenseActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etCategory: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnDate: Button
    private lateinit var btnSave: Button
    private lateinit var btnPickImage: Button
    private lateinit var btnViewExpense: Button

    private lateinit var db: BudgetDatabase
    private var selectedDate: String = ""

    private lateinit var imageView: ImageView
    private var imageByteArray: ByteArray? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Show selected image in ImageView
            imageView.setImageURI(it)

            // Convert image to ByteArray (so we can save it in Room)
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            imageByteArray = inputStream?.readBytes()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        etName = findViewById(R.id.etExpenseName)
        etCategory = findViewById(R.id.etCategory)
        etAmount = findViewById(R.id.etExpenseAmount)
        etDescription = findViewById(R.id.etExpenseDescription)
        btnDate = findViewById(R.id.btnPickDate)
        btnSave = findViewById(R.id.btnSaveExpense)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnViewExpense = findViewById(R.id.btnViewExpense)

        db = BudgetDatabase.getDatabase(this)


        // Connect views to variables
        imageView = findViewById(R.id.imageViewReceipt)
        // When pick image button is clicked, open gallery
        btnPickImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnDate.setOnClickListener {
            showDatePickerDialog()
        }

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                btnDate.text = selectedDate
            }, year, month, day)

        datePickerDialog.show()
    }

    fun view(view: View){
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }


        Thread {
            val userExpenses = db.expenseDao().getExpensesByUser(userId)
            runOnUiThread {
                if (userExpenses.isEmpty()) {
                    Toast.makeText(this, "No expenses found for this user", Toast.LENGTH_SHORT).show()
                } else {
                    val dialogView = layoutInflater.inflate(R.layout.activity_expense_list, null)
                    val container = dialogView.findViewById<LinearLayout>(R.id.expenseListLayout)

                    for (expense in userExpenses) {
                        val itemLayout = LinearLayout(this)
                        itemLayout.orientation = LinearLayout.VERTICAL
                        itemLayout.setPadding(0, 0, 0, 24)

                        val textView = TextView(this)
                        textView.text = "Name: ${expense.name}\nCategory: ${expense.category}\nAmount: R${expense.amount}\nDate: ${expense.date}"
                        textView.textSize = 16f

                        itemLayout.addView(textView)

                        if (expense.image != null) {
                            val imageView = ImageView(this)
                            imageView.setImageBitmap(
                                android.graphics.BitmapFactory.decodeByteArray(
                                    expense.image, 0, expense.image.size
                                )
                            )
                            imageView.layoutParams = LinearLayout.LayoutParams(200, 200)
                            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                            itemLayout.addView(imageView)
                        }

                        container.addView(itemLayout)
                    }

                    android.app.AlertDialog.Builder(this)
                        .setTitle("Your Expenses")
                        .setView(dialogView)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }.start()





        /*Thread {
            val userExpenses = db.expenseDao().getExpensesByUser(userId)
           runOnUiThread {
                if (userExpenses.isEmpty()) {
                    Toast.makeText(this, "No expenses found for this user", Toast.LENGTH_SHORT).show()
                } else {
                    val summary = userExpenses.joinToString("\n\n") {
                        "Name: ${it.name}\nCategory: ${it.category}\nAmount: R${it.amount}\nDate: ${it.date}"
                    }

                    // You can use an AlertDialog to show it for now
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Your Expenses")
                        .setMessage(summary)
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }.start()*/
    }

    fun saveExpense(view: View) {
        val name = etName.text.toString()
        val category = etCategory.text.toString()
        val amountText = etAmount.text.toString()
        val description = etDescription.text.toString()

        if (name.isEmpty() || category.isEmpty() || amountText.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and pick a date", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDouble()

        // Get user ID passed from login
        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_LONG).show()
            return
        }

        val expense = Expense(
            userOwnerId = userId,
            name = name,
            category = category,
            amount = amount,
            date = selectedDate,
            description = description,
            image = imageByteArray // This is your receipt image
        )

        Thread {
            db.expenseDao().insertExpense(expense)
        }.start()

        Toast.makeText(this, "Expense saved", Toast.LENGTH_LONG).show()
        finish()
    }

}