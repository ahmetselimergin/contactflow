package com.example.contactflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(navController: NavController) {
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("Online") }
    var meetingLink by remember { mutableStateOf("") }
    var limited by remember { mutableStateOf(false) }
    var participantsLimit by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    val tags = listOf("Sport", "Food", "Book", "Game", "Music", "Other")
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Event") },
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO: Create Event */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Create Event")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text("Event Date") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Location Type", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = locationType == "Online",
                    onClick = { locationType = "Online" }
                )
                Text("Online")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = locationType == "Physical",
                    onClick = { locationType = "Physical" }
                )
                Text("Physical")
            }

            if (locationType == "Online") {
                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Button(onClick = { /* TODO: Open map */ }) {
                    Text("Choose a location")
                }
            }

            Text("Participants Number", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !limited,
                    onClick = { limited = false }
                )
                Text("No limit")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = limited,
                    onClick = { limited = true }
                )
                Text("Limited")
            }

            if (limited) {
                OutlinedTextField(
                    value = participantsLimit,
                    onValueChange = { participantsLimit = it },
                    label = { Text("Enter a value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text("Event Fee", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isPaid,
                    onClick = { isPaid = false }
                )
                Text("Free")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = isPaid,
                    onClick = { isPaid = true }
                )
                Text("Paid")
            }

            if (isPaid) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // TODO: Replace with a dropdown
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency") },
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }

            Text("Tags", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = {
                            selectedTags = if (tag in selectedTags) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        },
                        label = { Text(tag) }
                    )
                }
            }

            Button(onClick = { /* TODO: Pick image */ }) {
                Text("Event Image")
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}