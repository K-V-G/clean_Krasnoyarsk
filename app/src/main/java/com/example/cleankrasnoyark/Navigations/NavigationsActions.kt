package com.example.cleankrasnoyark.Navigations

sealed class NavigationsActions(val route: String) {
    object Initial: NavigationsActions("initial_view")
    object Main: NavigationsActions("main_view")
}