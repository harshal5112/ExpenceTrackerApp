package com.example.expencetracker1.uii

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expencetracker1.data.Expense
import com.example.expencetracker1.data.ExpenseRepository
import java.text.SimpleDateFormat
import java.util.*

val orange = Color(0xFFFFA500)

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
            FloatingActionButton(onClick = { showAddExpenseDialog = true }, containerColor = orange) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "Total Expenses: ₹${String.format("%.2f", totalExpenses ?: 0.0) }", // Handle null case
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
                color = orange
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp).height(100.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(expense.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "₹${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(32.dp))
                }
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
            }, colors = ButtonDefaults.buttonColors(containerColor = orange)) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = orange)) {
                Text("Cancel")
            }
        }
    )
}
