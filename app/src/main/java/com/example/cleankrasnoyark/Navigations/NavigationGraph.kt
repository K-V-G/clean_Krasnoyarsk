package com.example.cleankrasnoyark.Navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cleankrasnoyark.initialViewsFunc
import com.example.cleankrasnoyark.mainViewFunc

@Composable
fun sutupNavigationGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController,
        startDestination = NavigationsActions.Initial.route)
    {
        composable(route = NavigationsActions.Initial.route) {
            initialViewsFunc(navController = navHostController)
        }

        composable(route = NavigationsActions.Main.route) {
            mainViewFunc(navHostController = navHostController)
        }
    }

}