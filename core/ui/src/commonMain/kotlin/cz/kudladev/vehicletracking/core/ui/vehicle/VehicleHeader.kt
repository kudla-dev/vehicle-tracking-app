package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.unknownBrand

@Composable
fun VehicleHeader(
    modifier: Modifier = Modifier,
    vehicle: Vehicle,
    onClick: () -> Unit
){
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = vehicle.brand?.name ?: stringResource(Res.string.unknownBrand),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${vehicle.model} ${vehicle.year} ${vehicle.color}",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = vehicle.spz,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            imageUrl = vehicle.images.firstOrNull()?.url ?: "",
            modifier = Modifier
                .width(150.dp)
                .clip(MaterialTheme.shapes.medium)
        )
    }
}

@Composable
fun VehicleHeaderSkeleton(
    modifier: Modifier = Modifier
) {
    val brandTextHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }
    val detailsTextHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }
    val spzTextHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }

    Row(
        modifier = modifier
            .shimmer()
            .clip(MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Brand placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(brandTextHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )

            // Vehicle details placeholder (2 lines)
            repeat(2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(detailsTextHeight)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                )
            }

            // SPZ placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(spzTextHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )
        }

        // Image placeholder
        Box(
            modifier = Modifier
                .width(150.dp)
                .aspectRatio(16f / 9f)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.Gray)
        )
    }
}

@Preview
@Composable
fun VehicleHeaderSkeletonPreview() {
    AppTheme {
        Surface {
            VehicleHeaderSkeleton(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun VehicleHeaderPreview() {
    AppTheme {
        Surface {
            VehicleHeader(
                modifier = Modifier.fillMaxWidth(),
                vehicle = testVehicle,
                onClick = {}
            )
        }
    }
}