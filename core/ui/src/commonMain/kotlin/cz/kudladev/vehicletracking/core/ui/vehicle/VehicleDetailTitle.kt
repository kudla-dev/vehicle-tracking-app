package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.displayFontFamily
import cz.kudladev.vehicletracking.model.Vehicle

@Composable
fun VehicleDetailTitle(
    vehicle: Vehicle
){
    Text(
        text = vehicle.fullName,
        maxLines = 1,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 18.sp,
            maxFontSize = 24.sp,
            stepSize = 1.sp
        ),
        fontFamily = displayFontFamily(),
        fontStyle = FontStyle.Italic,
    )
}

@Composable
fun VehicleDetailTitleSkeleton(){
    Box(
        modifier = Modifier
            .shimmer()
            .size(200.dp)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.large
            )
    )
}