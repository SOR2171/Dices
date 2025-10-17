package com.github.sor2171.dices.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.sor2171.dices.data.DiceDataCollection

@Composable
fun DiceTypeCreateDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    @StringRes titleStringRes: Int,
    @StringRes contentStringRes: Int,
    @StringRes confirmStringRes: Int,
    @StringRes dismissStringRes: Int
) {
    var numberError by remember { mutableStateOf(false) }
    var numberText by remember { mutableStateOf("6") }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(256.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 18.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(titleStringRes),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(contentStringRes),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                )

                TextField(
                    value = numberText,
                    onValueChange = { value ->
                        numberText = value
                        val number = value.toIntOrNull() ?: -1
                        numberError = number < 2
                    },
                    isError = numberError,
                    prefix = { Text("D") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .width(82.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        enabled = !numberError,
                        onClick = { onDismissRequest() },
                        modifier = Modifier
                    ) {
                        Text(stringResource(dismissStringRes))
                    }

                    TextButton(
                        enabled = !numberError,
                        onClick = {
                            if (DiceDataCollection.addDiceType(numberText.toIntOrNull()?: -1))
                                onConfirmation()
                            else numberError = true
                        },
                        modifier = Modifier
                    ) {
                        Text(stringResource(confirmStringRes))
                    }
                }
            }
        }
    }
}