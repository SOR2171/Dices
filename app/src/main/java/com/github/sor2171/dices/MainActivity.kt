package com.github.sor2171.dices

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.sor2171.dices.data.ScreenDestination
import com.github.sor2171.dices.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainFunc()
        }
    }
}

@Composable
fun MainFunc() {
    var diceRefresh by remember { mutableStateOf(true) }
    val lazyGridState = rememberLazyGridState()
    val navController = rememberNavController()

    val jumpToScreen: (String) -> Unit = { navController.navigate(it) }

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = ScreenDestination.MS
        ) {
            composable(ScreenDestination.MS) {
                MainScreen(
                    diceRefresh = diceRefresh,
                    changeDiceRefresh = { diceRefresh = it },
                    lazyGridState = lazyGridState,
                    screenToSI = { jumpToScreen(ScreenDestination.SI) },
                    screenToMD = { jumpToScreen(ScreenDestination.MD) }
                )
            }

            composable(ScreenDestination.SI) {
                StatisticalInformationScreen()
            }

            composable(ScreenDestination.MD) {
                ManageDiceScreen()
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun MainActivityPreview() {
    MainFunc()
}