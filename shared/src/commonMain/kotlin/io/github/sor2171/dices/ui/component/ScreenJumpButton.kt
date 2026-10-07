package io.github.sor2171.dices.ui.component

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScreenJumpButton(
    modifier: Modifier,
    jumpScreen: () -> Unit,
    textID: StringResource
) {
    Button(
        onClick = jumpScreen,
        modifier = modifier
    ) {
        Text(
            text = stringResource(textID),
            style = MaterialTheme.typography.titleLarge
        )
    }
}