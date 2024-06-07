package com.example.littlelemon

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.littlelemon.ui.composables.Onboarding

@Composable
fun MyNavigation(navController: NavHostController, startDestination: String) {
    val navigate: (Destinations) -> Unit =
        { destination -> navController.navigate(destination.route) }

    val navigateInclusive: (Destinations) -> Unit =
        { destination ->
            navController.navigate(destination.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Home.route) {
            Home(navigate)
        }
        composable(Onboarding.route) {
            Onboarding(navigate)
        }
        composable(Profile.route) {
            Profile(navigateInclusive)
        }

    }
}