package cz.kudladev.vehicletracking.core.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.currentDistance

@Composable
fun UserDistanceProgress(
    modifier: Modifier = Modifier,
    distance: Int,
    maximumDistance: Int,
){
    val distanceOverflow = distance >= maximumDistance

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = stringResource(Res.string.currentDistance),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "$distance Km / $maximumDistance Km",
                style = MaterialTheme.typography.labelLarge,
                color = if (distanceOverflow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            LinearProgressIndicator(
                progress = {
                    distance.toFloat() / maximumDistance.toFloat()
                },
                drawStopIndicator = {

                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun UserDistanceHalfProgressPreview() {
    AppTheme {
        Surface {
            UserDistanceProgress(
                distance = 1000,
                maximumDistance = 2000
            )
        }
    }
}

@Preview
@Composable
private fun UserDistanceFullProgressPreview() {
    AppTheme {
        Surface {
            UserDistanceProgress(
                distance = 2000,
                maximumDistance = 2000
            )
        }
    }
}

@Preview
@Composable
private fun UserDistanceOverflowProgressPreview() {
    AppTheme {
        Surface {
            UserDistanceProgress(
                distance = 2500,
                maximumDistance = 2000
            )
        }
    }
}
