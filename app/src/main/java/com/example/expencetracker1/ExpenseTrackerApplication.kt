package com.example.expencetracker1


import android.app.Application
import com.example.expencetracker1.data.ExpenseDatabase
import com.example.expencetracker1.data.ExpenseRepository

class ExpenseTrackerApplication : Application() {
    val database by lazy { ExpenseDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao()) }
}