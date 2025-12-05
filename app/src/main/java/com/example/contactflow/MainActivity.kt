package com.example.contactflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactflow.ui.screens.CreateEventScreen
import com.example.contactflow.ui.screens.EventDetailScreen
import com.example.contactflow.ui.screens.EventListScreen
import com.example.contactflow.ui.screens.FilterScreen
import com.example.contactflow.ui.screens.LoginScreen
import com.example.contactflow.ui.screens.MyEventsScreen
import com.example.contactflow.ui.screens.ProfileScreen
import com.example.contactflow.ui.screens.SignupScreen
import com.example.contactflow.ui.screens.WelcomeScreen
import com.example.contactflow.ui.theme.ContactFlowTheme
import com.example.contactflow.viewmodel.EventViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val eventViewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactFlowTheme {
                ContactFlowApp(eventViewModel)
            }
        }
    }
}

@Composable
fun ContactFlowApp(eventViewModel: EventViewModel) {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) "eventList" else "welcome"

    NavHost(navController = navController, startDestination = startDestination) { 
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("signup") { SignupScreen(navController = navController) }
        composable("eventList") { EventListScreen(navController = navController, eventViewModel = eventViewModel) }
        composable(
            route = "eventDetail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val event by eventViewModel.getEventById(eventId!!).collectAsState()
            if (event != null) {
                EventDetailScreen(event = event!!, navController = navController)
            }
        }
        composable("createEvent") { CreateEventScreen(navController = navController) }
        composable("myEvents") { MyEventsScreen(navController = navController, eventViewModel = eventViewModel) }
        composable("profile") { ProfileScreen(navController = navController) }
        composable("filter") { FilterScreen(navController = navController) }
    }
}
