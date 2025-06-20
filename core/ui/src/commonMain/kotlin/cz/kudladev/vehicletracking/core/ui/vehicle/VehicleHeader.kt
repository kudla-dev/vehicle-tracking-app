package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.ui.tooling.preview.Preview

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
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = vehicle.brand?.name ?: "Unknown Brand",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                text = "License Plate: ${vehicle.spz}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            imageUrl = vehicle.images.firstOrNull()?.url ?: "",
            modifier = Modifier
                .width(150.dp)
                .clip(MaterialTheme.shapes.medium)
                .padding(start = 16.dp),
        )
    }
}

private val testVehicle = Vehicle(
    brand = Brand(
        id = 1,
        name = "Test Brand",
        logoURL = ""
    ),
    fullName = "Test Vehicle",
    color = "Red",
    year = "2023",
    model = "Model X",
    spz = "1234 ABC",
    transferableSpz = false,
    maximumDistance = 0,
    totalDistance = 0,
    place = "Ostrava",
    driverLicense = "A1",
    images = emptyList()
)

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