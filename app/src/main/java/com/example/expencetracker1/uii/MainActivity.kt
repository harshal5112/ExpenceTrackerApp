package com.example.expencetracker1.uii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.expencetracker1.ExpenseTrackerApplication
import com.example.expencetracker1.ui.theme.ExpenceTracker1Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenceTracker1Theme {
                val application = application as ExpenseTrackerApplication
                ExpenseTrackerApp(application.repository)
            }
        }
    }
}
