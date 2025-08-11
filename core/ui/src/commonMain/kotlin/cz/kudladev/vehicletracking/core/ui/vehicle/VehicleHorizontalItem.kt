package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.twotone.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.Badge
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VehicleHorizontalItem(
    modifier: Modifier = Modifier,
    vehicle: Vehicle,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f/1f),
            ) {
                SwipeablePhotos(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.large),
                    images = vehicle.images,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = "${vehicle.model} ${vehicle.year} ${vehicle.color}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = vehicle.brand?.name?.uppercase() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Badge(
                        text = vehicle.place,
                        icon = Icons.Default.Place,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        vehicle.driverLicenses.forEach {
                            Badge(
                                text = it.type,
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleHorizontalItemSkeleton(
    modifier: Modifier = Modifier
) {
    val textHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }
    val labelHeight = with(LocalDensity.current) {
        MaterialTheme.typography.labelSmall.lineHeight.toDp()
    }

    Card(
        modifier = Modifier
            .shimmer()
            .then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image area placeholder
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f/1f)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color.Gray)
            )

            // Text content placeholders
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                // Vehicle title placeholder (2 lines)
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(textHeight)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.Gray)
                    )

                    if (it == 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // Brand name placeholder
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(100.dp)
                        .height(labelHeight)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                )

                // Bottom row placeholders
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(24.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                    )

                    Box(
                        modifier = Modifier
                            .width(75.dp)
                            .height(24.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun VehicleHorizontalItemSkeletonPreview() {
    AppTheme {
        VehicleHorizontalItemSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}


@Preview
@Composable
private fun VehicleHorizontalItemPreview() {
    AppTheme {
        VehicleHorizontalItem(
            vehicle = testVehicle,
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}