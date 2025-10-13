package com.github.sor2171.dices

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.data.DiceDataCollection
import com.github.sor2171.dices.ui.component.*
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

    AppTheme(
        dynamicColor = false
    ) {
        Scaffold(
            floatingActionButton = {
                RollFloatingActionButton(
                    extended = lazyGridState.isScrollInProgress,
                    textID = R.string.RFAButton_name,
                    onClick = {
                        diceRefresh = !diceRefresh
                        DiceDataCollection.rollAll()
                    }
                )
            }
        ) { paddingValues ->
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScreenJumpButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(vertical = 12.dp),
                        textID = R.string.SIButton_name
                    )

                    ScreenJumpButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(vertical = 12.dp),
                        textID = R.string.MDButton_name
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        DiceValueGrid(
                            key = diceRefresh
                        )
                    }
                }
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