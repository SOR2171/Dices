package io.github.sor2171.dices.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dices.shared.generated.resources.Res
import dices.shared.generated.resources.statistics_average_label
import dices.shared.generated.resources.statistics_dice_count_label
import dices.shared.generated.resources.statistics_empty
import dices.shared.generated.resources.statistics_max_label
import dices.shared.generated.resources.statistics_min_label
import dices.shared.generated.resources.statistics_overview_title
import dices.shared.generated.resources.statistics_sum_label
import dices.shared.generated.resources.statistics_type_count_label
import dices.shared.generated.resources.statistics_values_label
import io.github.sor2171.dices.core.entity.DiceType
import io.github.sor2171.dices.core.utils.DiceDataCollection
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun StatisticalInformationScreen() {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (DiceDataCollection.diceList.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(Res.string.statistics_empty),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    StatisticsOverviewCard()
                }
                items(DiceDataCollection.diceList) { diceType ->
                    DiceStatisticsCard(diceType)
                }
            }
        }
    }
}

@Composable
private fun StatisticsOverviewCard() {
    val diceList = DiceDataCollection.diceList
    val diceCount = diceList.sumOf { diceType -> diceType.diceValue.size }
    val valueSum = diceList.sumOf { diceType -> diceType.diceValue.sum() }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.statistics_overview_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            StatisticsRow(Res.string.statistics_type_count_label, diceList.size.toString())
            StatisticsRow(Res.string.statistics_dice_count_label, diceCount.toString())
            StatisticsRow(Res.string.statistics_sum_label, valueSum.toString())
        }
    }
}

@Composable
private fun DiceStatisticsCard(diceType: DiceType) {
    val values = diceType.diceValue
    val count = values.size
    val valueSum = values.sum()

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = diceType.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            StatisticsRow(Res.string.statistics_dice_count_label, count.toString())
            StatisticsRow(Res.string.statistics_sum_label, valueSum.toString())
            StatisticsRow(Res.string.statistics_average_label, averageText(valueSum, count))
            StatisticsRow(Res.string.statistics_max_label, (values.maxOrNull() ?: 0).toString())
            StatisticsRow(Res.string.statistics_min_label, (values.minOrNull() ?: 0).toString())
            StatisticsRow(Res.string.statistics_values_label, values.joinToString(" "))
        }
    }
}

@Composable
private fun StatisticsRow(
    label: StringResource,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(1f)
        )
    }
}

private fun averageText(valueSum: Int, count: Int): String {
    if (count == 0) return "0.0"
    val tenths = (valueSum * 10.0 / count).roundToInt()
    return "${tenths / 10}.${tenths % 10}"
}

@Preview
@Composable
fun StatisticalInformationScreenPreview() {
    MaterialTheme {
        StatisticalInformationScreen()
    }
}
