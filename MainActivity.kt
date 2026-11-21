package com.example.mypersonalassistant

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AssistantApp() }
    }
}
private enum class Tab(val label: String) {
    Home("Home"), Notes("Notes"), Reminders("Reminders"), Routine("Routine"), Money("Money")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantApp() {
    var tab by remember { mutableStateOf(Tab.Home) }
    var notes by remember { mutableStateOf(listOf("Welcome to My Personal Assistant")) }
    var tasks by remember { mutableStateOf(listOf("Plan today's priorities", "Review expenses")) }
    var expenses by remember { mutableStateOf(listOf("Groceries" to 450, "Travel" to 120)) }
    var income by remember { mutableIntStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val speech = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val words = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!words.isNullOrBlank()) {
            when (tab) {
                Tab.Money -> {
                    val value = Regex("\\d+").find(words)?.value?.toIntOrNull()
                    if (value != null) expenses = expenses + (words to value) else notes = notes + words
                }
                Tab.Reminders, Tab.Routine -> tasks = tasks + words
                else -> notes = notes + words
            }
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF6750A4))) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Column {
                        Text("My Personal Assistant", fontWeight = FontWeight.Bold)
                        Text("Your day, organised", style = MaterialTheme.typography.labelSmall)
                    } },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a note, task or expense")
                            }
                            runCatching { speech.launch(intent) }
                        }) { Icon(Icons.Default.Mic, contentDescription = "Voice input") }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item, onClick = { tab = item },
                            icon = { Icon(when(item) {
                                Tab.Home -> Icons.Default.Home
                                Tab.Notes -> Icons.Default.StickyNote2
                                Tab.Reminders -> Icons.Default.Notifications
                                Tab.Routine -> Icons.Default.CalendarMonth
                                Tab.Money -> Icons.Default.AccountBalanceWallet
                            }, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { description = ""; amount = ""; showAdd = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when(tab) {
                    Tab.Home -> {
                        item { Text("Good day 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
                        item { Text("Your notes, plans and money in one place.") }
                        item { Summary("Notes", notes.size.toString(), Icons.Default.StickyNote2) }
                        item { Summary("Open tasks", tasks.size.toString(), Icons.Default.CheckCircle) }
                        item { Summary("Expenses", "₹${expenses.sumOf { it.second }}", Icons.Default.Wallet) }
                        item { Summary("Income", "₹$income", Icons.Default.TrendingUp) }
                    }
                    Tab.Notes -> {
                        item { Text("Your notes", style = MaterialTheme.typography.headlineSmall) }
                        items(notes) { SimpleCard(it) }
                    }
                    Tab.Reminders -> {
                        item { Text("Reminders & tasks", style = MaterialTheme.typography.headlineSmall) }
                        item { Text("Tasks are listed here. Scheduled alerts will be added in the next build stage.") }
                        items(tasks) { SimpleCard(it) }
                    }
                    Tab.Routine -> {
                        item { Text("Daily routine", style = MaterialTheme.typography.headlineSmall) }
                        items(listOf("Morning routine", "Work / study", "Meals & breaks", "Exercise", "Evening review")) { SimpleCard(it) }
                    }
                    Tab.Money -> {
                        item { Text("Money Manager", style = MaterialTheme.typography.headlineSmall) }
                        item { Summary("Total expenses", "₹${expenses.sumOf { it.second }}", Icons.Default.TrendingDown) }
                        item { Summary("Income", "₹$income", Icons.Default.TrendingUp) }
                        item { Text("Transactions", style = MaterialTheme.typography.titleLarge) }
                        items(expenses) { (label, value) -> SimpleCard("$label — ₹$value") }
                    }
                }
            }
        }

        if (showAdd) {
            AlertDialog(
                onDismissRequest = { showAdd = false },
                title = { Text(if (tab == Tab.Money) "Add expense" else if (tab == Tab.Reminders || tab == Tab.Routine) "Add task" else "Add note") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(description, { description = it }, label = { Text("Description") })
                        if (tab == Tab.Money) OutlinedTextField(amount, { amount = it.filter(Char::isDigit) }, label = { Text("Amount (₹)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (description.isNotBlank()) when(tab) {
                            Tab.Money -> expenses = expenses + (description to (amount.toIntOrNull() ?: 0))
                            Tab.Reminders, Tab.Routine -> tasks = tasks + description
                            else -> notes = notes + description
                        }
                        showAdd = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancel") } }
            )
        }
    }
}
@Composable
private fun Summary(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
@Composable private fun SimpleCard(text: String) {
    Card { Text(text, Modifier.fillMaxWidth().padding(16.dp)) }
}
