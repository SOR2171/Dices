package com.github.sor2171.dices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.sor2171.dices.ui.theme.DicesTheme

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
    DicesTheme {
        Scaffold(

        ) { paddingValues ->
            Text("")
            val p = paddingValues
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    MainFunc()
}