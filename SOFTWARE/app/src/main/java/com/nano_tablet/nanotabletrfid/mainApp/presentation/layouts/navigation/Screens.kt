package com.nano_tablet.nanotabletrfid.mainApp.presentation.layouts.navigation

sealed class Screen(val route: String) {
    data object Instruments : Screen("INSTRUMENTS")
    data object MyReservations : Screen("MY_RESERVATIONS")
    data object Selection : Screen("SELECTION")
    data object LogIn : Screen("LOGIN")
    data object MainPage : Screen("MAIN_PAGE")
    data object RegisterCard : Screen("REGISTER_CARD")
    data object BookingSystem : Screen("BOOKING_SYSTEM")
    data object Onboarding : Screen("ONBOARDING")
}