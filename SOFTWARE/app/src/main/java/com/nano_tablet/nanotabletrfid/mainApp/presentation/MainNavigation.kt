package com.nano_tablet.nanotabletrfid.mainApp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation.Screen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.LogInScreen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.BookingSystemScreen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.MainScreen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.screens.PlanningBoardScreen
import com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.view_models.SharedViewModel

/**
 * App-level navigation host.
 *
 * Graph:
 * Onboarding (graph)
 *  ├─ LogIn
 *  ├─ MainPage
 *  ├─ PlanningBoard
 *  └─ RegisterCard
 */
@Composable
fun MainNavigation(
) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {
        navigation(
            Screen.LogIn.route,
            route = Screen.Onboarding.route
        ) {
            composable(Screen.LogIn.route) { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedState.collectAsStateWithLifecycle()

                LogInScreen(
                    onNavigate = {
                        viewModel.updateState()
                        navController.navigate(Screen.MainPage.route)
                    },
                    onNavigatePlanningBoard = {
                        navController.navigate(Screen.BookingSystem.route)
                    },
                    onNavigateRegisterCard = {
                        navController.navigate(Screen.RegisterCard.route)
                    },
                    sharedViewModel = viewModel
                )
            }

            composable(Screen.MainPage.route) { entry ->
                val viewModel = entry.sharedViewModel<SharedViewModel>(navController)
                val state by viewModel.sharedState.collectAsStateWithLifecycle()
                MainScreen(
                    onNavigationFinished = {
                        viewModel.updateState()
                        navController.navigate(Screen.LogIn.route) {
                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }
                    },
                    sharedState = state,
                    sharedViewModel = viewModel
                )
            }

            composable(Screen.BookingSystem.route) { entry ->
                PlanningBoardScreen(
                    onNavigate = {
                        navController.navigate(Screen.LogIn.route) {
                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }
                    })
            }
            composable(Screen.BookingSystem.route) { entry ->
                BookingSystemScreen(
                    onNavigate = {
                        navController.navigate("login_screen") {
                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }
                    })
            }
        }
    }
}

/**
 * Provides a shared ViewModel scoped to the parent navigation graph.
 * Ensures both siblings within the same graph see the same instance.
 */
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(key1 = this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
