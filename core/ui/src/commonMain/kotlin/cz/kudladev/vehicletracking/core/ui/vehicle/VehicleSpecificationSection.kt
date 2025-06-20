package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VehicleSpecificationSection(
    modifier: Modifier = Modifier,
    vehicle: Vehicle
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Brand",
                value = vehicle.brand?.name
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Model",
                value = vehicle.model
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Year",
                value = vehicle.year
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Color",
                value = vehicle.color
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Place",
                value = vehicle.place
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Driver License",
                value = vehicle.driverLicense
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "SPZ",
                value = vehicle.spz
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = "Transferable SPZ",
                value = if (vehicle.transferableSpz) "Yes" else "No"
            )
        }
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
private fun VehicleSpecificationSectionPreview(){
    AppTheme {
        VehicleSpecificationSection(
            vehicle = testVehicle
        )
    }
}

@Composable
private fun VehicleSpecificationItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String?
){
    Column(
        modifier = modifier
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}



@Preview
@Composable
private fun VehicleSpecificationItemPreview(){
    AppTheme {
        VehicleSpecificationItem(
            title = "Brand",
            value = testVehicle.brand?.name
        )
    }
}

@Preview
@Composable
private fun VehicleSpecificationItemNullPreview(){
    AppTheme {
        VehicleSpecificationItem(
            title = "Brand",
            value = null
        )
    }
}