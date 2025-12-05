package com.example.contactflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactflow.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _myEvents = MutableStateFlow<List<Event>>(emptyList())
    val myEvents: StateFlow<List<Event>> = _myEvents

    fun getEventById(eventId: String): StateFlow<Event?> {
        val eventFlow = MutableStateFlow<Event?>(null)
        viewModelScope.launch {
            db.collection("events").document(eventId).get()
                .addOnSuccessListener { document ->
                    val event = document.toObject(Event::class.java)?.copy(id = document.id)
                    eventFlow.value = event
                }
        }
        return eventFlow
    }

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            db.collection("events")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        // Handle error
                        return@addSnapshotListener
                    }

                    val eventList = snapshots?.map { document ->
                        document.toObject(Event::class.java).copy(id = document.id)
                    } ?: emptyList()

                    _events.value = eventList
                }
        }
    }

    fun fetchMyEvents() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                db.collection("events").whereEqualTo("organizerId", userId)
                    .addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            // Handle error
                            return@addSnapshotListener
                        }

                        val eventList = snapshots?.map { document ->
                            document.toObject(Event::class.java).copy(id = document.id)
                        } ?: emptyList()

                        _myEvents.value = eventList
                    }
            }
        }
    }

    fun deleteEvent(eventId: String, imageUrl: String?) {
        viewModelScope.launch {
            db.collection("events").document(eventId).delete()
                .addOnSuccessListener {
                    imageUrl?.let { url ->
                        if (url.isNotEmpty()) {
                            storage.getReferenceFromUrl(url).delete()
                        }
                    }
                }
        }
    }
}
