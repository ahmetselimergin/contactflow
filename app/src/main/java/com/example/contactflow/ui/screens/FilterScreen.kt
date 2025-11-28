package com.example.contactflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavController) {
    var distance by remember { mutableFloatStateOf(15f) }
    var selectedDate by remember { mutableStateOf("This week") }
    var selectedCost by remember { mutableStateOf("Free") }
    var selectedType by remember { mutableStateOf("Online") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /*TODO*/ }) {
                    Text("Clear all")
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text("Show 68 events")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FilterSection("Dates") {
                val dates = listOf("Today", "This week", "Next 30 days", "Choose")
                FilterChipGroup(items = dates, selectedItem = selectedDate, onItemSelected = { selectedDate = it })
            }

            FilterSection("Location") {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search near me") },
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = "Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FilterSection("Distance", "Up to ${distance.toInt()} miles away") {
                Slider(
                    value = distance,
                    onValueChange = { distance = it },
                    valueRange = 1f..30f
                )
            }

            FilterSection("Cost") {
                val costs = listOf("Free", "Under $50", "Over $50")
                FilterChipGroup(items = costs, selectedItem = selectedCost, onItemSelected = { selectedCost = it })
            }
            
            FilterSection("Type") {
                val types = listOf("Offline", "Online")
                FilterChipGroup(items = types, selectedItem = selectedType, onItemSelected = { selectedType = it })
            }

            FilterSection("Categories", "View more") {
                val categories = listOf(
                    "Sports" to Icons.Default.SportsBasketball,
                    "Health" to Icons.Default.FavoriteBorder,
                    "Food and drink" to Icons.Default.Fastfood,
                )
                CategoryChips(categories = categories)
            }
        }
    }
}

@Composable
fun FilterSection(title: String, extra: String? = null, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (extra != null) {
                Text(extra, style = MaterialTheme.typography.bodyMedium)
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { item ->
            FilterChip(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                label = { Text(item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(categories: List<Pair<String, ImageVector>>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(categories) { (name, icon) ->
            SuggestionChip(
                onClick = { /* TODO */ },
                label = { Text(name) },
                icon = { Icon(icon, contentDescription = name, modifier = Modifier.size(18.dp)) }
            )
        }
    }
}
