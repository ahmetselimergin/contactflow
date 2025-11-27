package com.example.contactflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactflow.model.Event
import com.example.contactflow.ui.screens.CreateEventScreen
import com.example.contactflow.ui.screens.EventDetailScreen
import com.example.contactflow.ui.screens.EventListScreen
import com.example.contactflow.ui.screens.LoginScreen
import com.example.contactflow.ui.screens.ProfileScreen
import com.example.contactflow.ui.screens.SignupScreen
import com.example.contactflow.ui.screens.WelcomeScreen
import com.example.contactflow.ui.theme.ContactFlowTheme

object DataSource {
    val events = listOf(
        Event(
            id = 1,
            name = "Global Rugby Challenge",
            date = "Tue, 22 Oct",
            time = "09:00 AM",
            location = "Da Nang, Vietnam",
            description = "An exciting rugby match between the top teams from around the globe. Don't miss this epic clash!",
            imageUrl = "https://picsum.photos/seed/rugby/400/300",
            organizer = "Albert Flores",
            categories = listOf("Rugby", "Team sports"),
            friendsGoing = 6
        ),
        Event(
            id = 2,
            name = "Android Dev Summit",
            date = "Mon, 18 Nov",
            time = "10:00 AM",
            location = "Online",
            description = "The biggest event for Android developers. Learn about the latest trends and technologies.",
            imageUrl = "https://picsum.photos/seed/android/400/300",
            organizer = "Google",
            categories = listOf("Tech", "Android"),
            friendsGoing = 125
        )
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactFlowTheme {
                ContactFlowApp()
            }
        }
    }
}

@Composable
fun ContactFlowApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") { // Changed start destination
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { SignupScreen(navController = navController) }
        composable("eventList") { EventListScreen(navController = navController, events = DataSource.events) }
        composable(
            route = "eventDetail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId")
            val event = DataSource.events.find { it.id == eventId }
            if (event != null) {
                EventDetailScreen(event = event, navController = navController)
            }
        }
        composable("createEvent") { CreateEventScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
    }
}
