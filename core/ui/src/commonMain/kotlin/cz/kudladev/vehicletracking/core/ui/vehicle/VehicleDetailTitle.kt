package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.designsystem.theme.displayFontFamily
import cz.kudladev.vehicletracking.model.Vehicle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VehicleDetailTitle(
    modifier: Modifier = Modifier,
    vehicle: Vehicle
){
    Text(
        modifier = modifier,
        text = vehicle.fullName,
        maxLines = 2,
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
fun VehicleDetailTitleSkeleton(
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .shimmer()
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    Color.Gray,
                    shape = MaterialTheme.shapes.large
                )
        )
    }
}

@Preview
@Composable
private fun VehicleDetailTitlePreview(){
    AppTheme {
        Surface {
            VehicleDetailTitle(
                vehicle = testVehicle
            )
        }
    }
}

@Preview
@Composable
private fun VehicleDetailTitleSkeletonPreview(){
    AppTheme {
        Surface {
            VehicleDetailTitleSkeleton()
        }
    }
}
