package com.github.sor2171.dices.ui.component


import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun ScreenJumpButton(
    modifier: Modifier,
    textID: Int
) {
    Button(
        onClick = {},
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = textID),
            style = MaterialTheme.typography.titleLarge
        )
    }
}