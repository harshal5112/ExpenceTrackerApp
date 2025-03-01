package com.example.expencetracker1.uii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expencetracker1.ExpenseTrackerApplication
import com.example.expencetracker1.data.Expense
import com.example.expencetracker1.data.ExpenseRepository
import com.example.expencetracker1.ui.theme.ExpenceTracker1Theme
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenceTracker1Theme {
                // Pass the application context to the composable
                val application = application as ExpenseTrackerApplication
                ExpenseTrackerApp(application.repository)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(repository: ExpenseRepository) {
    val viewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(repository)
    )
    val expenses by viewModel.allExpenses.observeAsState(emptyList())
    val totalExpenses by viewModel.totalExpenses.observeAsState()

    var showAddExpenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Expense Tracker") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpenseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "Total Expenses: ₹${String.format("%.2f", totalExpenses ?: 0.0)}", // Handle null case
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(expenses) { expense ->
                    ExpenseItem(expense, onDelete = { viewModel.delete(expense) })
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onAdd = { title, amount ->
                viewModel.insert(Expense(title = title, amount = amount))
                showAddExpenseDialog = false
            }
        )
    }
}
@Composable
fun ExpenseItem(expense: Expense, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
                Text(text = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(expense.date), style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "₹${String.format("%.2f", expense.amount)}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onAdd: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                onAdd(title, amountValue)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}