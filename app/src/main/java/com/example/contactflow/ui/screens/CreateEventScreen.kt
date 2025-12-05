package com.example.contactflow.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.contactflow.R
import com.example.contactflow.model.Currency
import com.example.contactflow.model.Event
import com.example.contactflow.model.LocationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navController: NavController) {
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf(LocationType.PHYSICAL) }
    var meetingLink by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isLimitedParticipants by remember { mutableStateOf(false) }
    var maxParticipants by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf(Currency.USD) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            eventDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val isFormValid by remember(eventName, eventDate, locationType, meetingLink, location, isLimitedParticipants, maxParticipants, isPaid, price, description) {
        derivedStateOf {
            eventName.isNotBlank() &&
                    eventDate.isNotBlank() &&
                    (if (locationType == LocationType.ONLINE) meetingLink.isNotBlank() else location.isNotBlank()) &&
                    (!isLimitedParticipants || maxParticipants.isNotBlank()) &&
                    (!isPaid || price.isNotBlank()) &&
                    description.isNotBlank()
        }
    }

    fun saveEvent(imageUrl: String? = null) {
        val db = FirebaseFirestore.getInstance()
        val eventRef = db.collection("events").document()
        val organizer = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown"
        val organizerId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val event = Event(
            id = eventRef.id,
            name = eventName,
            date = eventDate,
            time = "", // TODO: Add time picker
            locationType = locationType,
            meetingLink = if (locationType == LocationType.ONLINE) meetingLink else null,
            location = if (locationType == LocationType.PHYSICAL) location else "",
            isLimitedParticipants = isLimitedParticipants,
            maxParticipants = if (isLimitedParticipants) maxParticipants.toIntOrNull() else null,
            isPaid = isPaid,
            price = if (isPaid) price.toDoubleOrNull() else null,
            currency = if (isPaid) currency else null,
            tags = selectedTags.toList(),
            imageUrl = imageUrl ?: "",
            description = description,
            organizer = organizer,
            organizerId = organizerId
        )

        eventRef.set(event)
            .addOnSuccessListener {
                Toast.makeText(context, R.string.event_created_successfully, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "${context.getString(R.string.event_creation_failed)}: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_event_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text(stringResource(R.string.event_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text(stringResource(R.string.event_date_label)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                }
            )

            LocationTypeSelection(locationType, onLocationTypeChange = { locationType = it })

            if (locationType == LocationType.ONLINE) {
                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ParticipantsSelection(isLimitedParticipants, onLimitedChange = { isLimitedParticipants = it }, maxParticipants, onMaxParticipantsChange = { maxParticipants = it })

            FeeSelection(isPaid, onPaidChange = { isPaid = it }, price, onPriceChange = { price = it }, currency, onCurrencyChange = { currency = it })

            TagSelection(selectedTags, onTagSelected = { tag ->
                selectedTags = if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            })

            Button(onClick = { imagePickerLauncher.launch("image/png") }) {
                Text("Select Image")
            }

            imageUri?.let {
                Text("Selected Image: ${it.path}")
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.event_description_label)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Button(
                onClick = {
                    if (imageUri != null) {
                        val storageRef = FirebaseStorage.getInstance().reference.child("event_images/${UUID.randomUUID()}")
                        storageRef.putFile(imageUri!!)
                            .addOnSuccessListener { 
                                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                    saveEvent(downloadUrl.toString())
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        saveEvent()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(stringResource(R.string.create_button))
            }
        }
    }
}

@Composable
fun LocationTypeSelection(locationType: LocationType, onLocationTypeChange: (LocationType) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Location Type:")
        Spacer(modifier = Modifier.width(8.dp))
        RadioButton(
            selected = locationType == LocationType.PHYSICAL,
            onClick = { onLocationTypeChange(LocationType.PHYSICAL) }
        )
        Text("Physical")
        Spacer(modifier = Modifier.width(8.dp))
        RadioButton(
            selected = locationType == LocationType.ONLINE,
            onClick = { onLocationTypeChange(LocationType.ONLINE) }
        )
        Text("Online")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsSelection(
    isLimited: Boolean,
    onLimitedChange: (Boolean) -> Unit,
    maxParticipants: String,
    onMaxParticipantsChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = isLimited, onCheckedChange = onLimitedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Limited Participants")
        if (isLimited) {
            OutlinedTextField(
                value = maxParticipants,
                onValueChange = onMaxParticipantsChange,
                label = { Text("Max") },
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeSelection(
    isPaid: Boolean,
    onPaidChange: (Boolean) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    currency: Currency,
    onCurrencyChange: (Currency) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = isPaid, onCheckedChange = onPaidChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Paid Event")
        if (isPaid) {
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("Price") },
                modifier = Modifier.width(100.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Currency dropdown can be implemented here
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelection(selectedTags: Set<String>, onTagSelected: (String) -> Unit) {
    val tags = listOf("Sport", "Food", "Book", "Game", "Music", "Other")
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        tags.forEach { tag ->
            FilterChip(
                selected = selectedTags.contains(tag),
                onClick = { onTagSelected(tag) },
                label = { Text(tag) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
