package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.designsystem.theme.displayFontFamily
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Badge(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    text: String,
    textSize: TextUnit = 8.sp,
    iconSize: Dp = 8.dp,
    icon: ImageVector? = null
){

    val tilt = with(LocalDensity.current) { 4.dp.toPx() }
    Row(
        modifier = Modifier
            .then(modifier)
            .drawBehind {
                val path = Path().apply {
                    moveTo(tilt, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width - tilt , size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(
                    path = path,
                    color = containerColor,
                )
            }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize),
                tint = contentColor
            )
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = textSize,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontFamily = displayFontFamily()
        )
    }
}


@Preview
@Composable
fun BadgePreview() {
    AppTheme {
        Badge(
            text = "Ostrava",
            icon = Icons.Default.Place,
            modifier = Modifier
        )
    }
}