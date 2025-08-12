package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.DriverLicense
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.brand
import vehicletracking.core.ui.generated.resources.color
import vehicletracking.core.ui.generated.resources.driverLicense
import vehicletracking.core.ui.generated.resources.licensePlate
import vehicletracking.core.ui.generated.resources.model
import vehicletracking.core.ui.generated.resources.no
import vehicletracking.core.ui.generated.resources.place
import vehicletracking.core.ui.generated.resources.transferableLicensePlate
import vehicletracking.core.ui.generated.resources.year
import vehicletracking.core.ui.generated.resources.yes

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
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.brand),
                value = vehicle.brand?.name
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.model),
                value = vehicle.model
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.year),
                value = vehicle.year
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.color),
                value = vehicle.color
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.place),
                value = vehicle.place
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.driverLicense),
                value = vehicle.driverLicenses.joinToString(separator = ", ") { it.type }.ifBlank({ "N/A" })
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.licensePlate),
                value = vehicle.spz
            )
            VehicleSpecificationItem(
                modifier = Modifier.weight(1f),
                title = stringResource(Res.string.transferableLicensePlate),
                value = if (vehicle.transferableSpz) stringResource(Res.string.yes) else stringResource(Res.string.no)
            )
        }
    }
}

@Composable
fun VehicleSpecificationSectionSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.shimmer(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4){
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                VehicleSpecificationItemSkeleton(
                    modifier = Modifier.weight(1f)
                )
                VehicleSpecificationItemSkeleton(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VehicleSpecificationItemSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.Gray)
        )
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .width(120.dp)
                .height(20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(Color.Gray)
        )
    }
}

@Preview
@Composable
private fun VehicleSpecificationSectionSkeletonPreview() {
    AppTheme {
        Surface {
            VehicleSpecificationSectionSkeleton()
        }
    }
}

internal val testVehicle = Vehicle(
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
    driverLicenses = mutableSetOf(DriverLicense("A")),
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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