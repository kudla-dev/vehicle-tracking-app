package cz.kudladev.vehicletracking.core.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimeSelect(
    onDismiss: () -> Unit,
    times: List<LocalTime>,
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ){
        Column(
            modifier = Modifier
                .widthIn(300.dp,600.dp)
                .heightIn(300.dp,700.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Text(
                text = "Select time",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp),
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(times) { time ->
                    Text(
                        text = time.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                if (time == selectedTime) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 16.dp)
                            .clickable {
                                onTimeSelected(time)
                                onDismiss()
                            },
                        color = if (time == selectedTime) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun TimeSelectPreview() {
    AppTheme {
        TimeSelect(
            onDismiss = {},
            times = listOf(LocalTime(10, 0), LocalTime(12, 30), LocalTime(15, 45)),
            selectedTime = LocalTime(12, 30),
            onTimeSelected = {}
        )
    }
}