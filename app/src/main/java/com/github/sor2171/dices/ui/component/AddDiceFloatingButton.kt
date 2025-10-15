package com.github.sor2171.dices.ui.component


import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AddDiceFloatingActionButton(
    extended: Boolean,
    @StringRes textID: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            AnimatedVisibility(visible = extended) {
                Text(
                    text = stringResource(textID),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}