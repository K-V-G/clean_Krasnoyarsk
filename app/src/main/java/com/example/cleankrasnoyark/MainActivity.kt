package com.example.cleankrasnoyark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cleankrasnoyark.Navigations.sutupNavigationGraph
import com.example.cleankrasnoyark.ui.theme.CleanKrasnoyarkTheme

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            CleanKrasnoyarkTheme {
                navController = rememberNavController()
                sutupNavigationGraph(navController)
            }
        }
    }
}