package com.example.contactflow.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

enum class LocationType {
    ONLINE, PHYSICAL
}

enum class Currency {
    USD, EUR, TRY
}

@Keep
@Serializable
data class Event(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "", 
    val locationType: LocationType = LocationType.PHYSICAL,
    val meetingLink: String? = null,
    val location: String = "",
    val lat: Double? = null,
    val lng: Double? = null,
    val isLimitedParticipants: Boolean = false,
    val maxParticipants: Int? = null,
    val isPaid: Boolean = false,
    val price: Double? = null,
    val currency: Currency? = null,
    val tags: List<String> = emptyList(),
    val imageUrl: String = "",
    val description: String = "",
    val organizer: String = "",
    val organizerId: String = ""
)
