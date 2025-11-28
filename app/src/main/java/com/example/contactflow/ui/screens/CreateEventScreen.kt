package com.example.contactflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navController: NavController) {
    var eventName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("Online") }
    var meetingLink by remember { mutableStateOf("") }
    var limited by remember { mutableStateOf(false) }
    var participantsLimit by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    val currencies = listOf("USD", "EUR", "TRY")
    var currency by remember { mutableStateOf(currencies[0]) }
    var currencyExpanded by remember { mutableStateOf(false) }
    val tags = listOf("Sport", "Food", "Book", "Game", "Music", "Other")
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var description by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateTimeFormatter = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }

    // Picker states
    val startDatePickerState = rememberDatePickerState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(initialHour = calendar.get(Calendar.HOUR_OF_DAY), initialMinute = calendar.get(Calendar.MINUTE))
    var showStartTimePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState()
    var showEndDatePicker by remember { mutableStateOf(false) }
    val endTimePickerState = rememberTimePickerState(initialHour = calendar.get(Calendar.HOUR_OF_DAY), initialMinute = calendar.get(Calendar.MINUTE))
    var showEndTimePicker by remember { mutableStateOf(false) }

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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        label = { Text("Start Date") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Select date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable { showStartDatePicker = true })
                }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { },
                        label = { Text("End Date") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Select date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier
                        .matchParentSize()
                        .clickable { showEndDatePicker = true })
                }
            }

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
                    ExposedDropdownMenuBox(
                        expanded = currencyExpanded,
                        onExpandedChange = { currencyExpanded = !currencyExpanded },
                        modifier = Modifier.weight(0.5f)
                    ) {
                        OutlinedTextField(
                            value = currency,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = currencyExpanded,
                            onDismissRequest = { currencyExpanded = false }
                        ) {
                            currencies.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        currency = item
                                        currencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Text("Tags", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags) { tag ->
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

            val primaryColor = MaterialTheme.colorScheme.primary
            val stroke = remember { Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { /* TODO: Select file */ }
                        .drawBehind {
                            drawRoundRect(
                                color = primaryColor,
                                style = stroke,
                                cornerRadius = CornerRadius(8.dp.toPx())
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Select file"
                        )
                        Text(text = "Select file")
                    }
                }

                Text("or")

                Button(onClick = { /* TODO: Open camera */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null
                        )
                        Text("Open Camera & Take Photo")
                    }
                }
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

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showStartDatePicker = false; showStartTimePicker = true }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = startDatePickerState) }
    }

    if (showStartTimePicker) {
        Dialog(onDismissRequest = { showStartTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = startTimePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showStartTimePicker = false }) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                showStartTimePicker = false
                                startDatePickerState.selectedDateMillis?.let {
                                    calendar.timeInMillis = it
                                    calendar.set(Calendar.HOUR_OF_DAY, startTimePickerState.hour)
                                    calendar.set(Calendar.MINUTE, startTimePickerState.minute)
                                    startDate = dateTimeFormatter.format(calendar.time)
                                }
                            }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showEndDatePicker = false; showEndTimePicker = true }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = endDatePickerState) }
    }

    if (showEndTimePicker) {
        Dialog(onDismissRequest = { showEndTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = endTimePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEndTimePicker = false }) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                showEndTimePicker = false
                                endDatePickerState.selectedDateMillis?.let {
                                    calendar.timeInMillis = it
                                    calendar.set(Calendar.HOUR_OF_DAY, endTimePickerState.hour)
                                    calendar.set(Calendar.MINUTE, endTimePickerState.minute)
                                    endDate = dateTimeFormatter.format(calendar.time)
                                }
                            }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }
}
