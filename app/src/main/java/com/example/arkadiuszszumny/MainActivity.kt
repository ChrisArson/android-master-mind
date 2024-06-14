package com.example.arkadiuszszumny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.arkadiuszszumny.ui.screen.GameScreen
import com.example.arkadiuszszumny.ui.screen.ProfileScreen
import com.example.arkadiuszszumny.ui.screen.ResultsScreen

import com.example.arkadiuszszumny.ui.theme.ArkadiuszszumnyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArkadiuszszumnyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationGraph(navController)
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable(
            "login",
            enterTransition = {
                fadeIn(animationSpec = tween(1000)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            }
        ) {
            ProfileScreen(navController = navController)
        }
        composable(
            "game/{colors}/{playerId}",
            arguments = listOf(
                navArgument("colors") { type = NavType.IntType },
                navArgument("playerId") { type = NavType.LongType }
                ),
            enterTransition = {
                fadeIn(animationSpec = tween(1000)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            }
        ) { backStackEntry ->
            val colors = backStackEntry.arguments?.getInt("colors") ?: 0
            val playerId = backStackEntry.arguments?.getLong("playerId") ?: 0
            GameScreen(navController = navController, colors = colors, playerId = playerId)
        }
        composable(
            "results/{score}",
            arguments = listOf(navArgument("score") { type = NavType.IntType }),
            enterTransition = {
                fadeIn(animationSpec = tween(1000)) + slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(1000)) + slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            }
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ResultsScreen(
                score = score,
                onPlayAgain = {
                    val previousBackStackEntry = navController.previousBackStackEntry
                    val colors = previousBackStackEntry?.arguments?.getInt("colors") ?: 4
                    val playerId = previousBackStackEntry?.arguments?.getLong("playerId") ?: 0
                    navController.navigate("game/$colors/$playerId")
                },
                onLogout = {
                    navController.popBackStack("login", inclusive = false)
                }
            )
        }
    }
}