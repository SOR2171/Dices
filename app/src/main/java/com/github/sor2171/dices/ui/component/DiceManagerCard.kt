package com.github.sor2171.dices.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.sor2171.dices.R
import com.github.sor2171.dices.data.DiceDataCollection
import com.github.sor2171.dices.data.DiceType

@Composable
fun DiceManagerCard(
    diceType: DiceType
) {
    var numberError by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(diceType.diceValue.size.toString()) }

    Surface {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(4.dp)
                .padding(start = 16.dp)
                .height(48.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = diceType.name,
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp))
            {
                IconButton(
                    enabled = (!numberError && diceType.diceValue.isNotEmpty()),
                    onClick = {
                        diceType.mountTo(diceType.diceValue.size - 1)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_remove_24),
                        contentDescription = stringResource(R.string.minus_button),
                    )
                }
                TextField(
                    value = textFieldValue,
                    onValueChange = { value ->
                        textFieldValue = value
                        val number = value.toIntOrNull() ?: -1
                        if (number < 0) {
                            numberError = true
                        } else {
                            numberError = false
                            diceType.mountTo(number)
                        }
                    },
                    isError = numberError,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .width(96.dp)
                )

                IconButton(
                    onClick = {
                        diceType.mountTo(diceType.diceValue.size + 1)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.minus_button)
                    )
                }

                IconButton(
                    onClick = {
                        DiceDataCollection.deleteType(diceType.max)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_button)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun DiceManagerCardPreview() {
    DiceManagerCard(
        DiceType(3)
    )
}