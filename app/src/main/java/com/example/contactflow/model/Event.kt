package com.example.contactflow.model

data class Event(
    val id: Int,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val imageUrl: String = "", // Placeholder for an image URL
    val organizer: String,
    val categories: List<String>,
    val friendsGoing: Int
)
