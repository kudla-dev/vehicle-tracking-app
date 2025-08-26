package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.compareTo
import kotlin.math.pow
import kotlin.text.toInt

@Composable
fun VehicleMileageChart(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>,
    unit: String = "km",
) {
    val chartColor = MaterialTheme.colorScheme.primary
    val chartFillColor = chartColor.copy(alpha = 0.2f)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = textColor)
    val titleStyle = MaterialTheme.typography.titleMedium.copy(
        color = textColor,
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic
    )
    val surface = MaterialTheme.colorScheme.surface

    // State to track touch interaction
    var isTouching by remember { mutableStateOf(false) }
    var touchPosition by remember { mutableStateOf(Offset.Zero) }
    var selectedPointIndex by remember { mutableStateOf(-1) }

    // Process data - add starting point with 0f if not exactly 12 items
    val processedData = if (data.size != 12) {
        listOf("Start" to 0f) + data
    } else {
        data
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Mileage",
            style = MaterialTheme.typography.titleLarge,
            fontStyle = FontStyle.Italic,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .padding(top = 16.dp, bottom = 30.dp, start = 8.dp, end = 8.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isTouching = true
                            touchPosition = offset
                        },
                        onDragEnd = {
                            isTouching = false
                            selectedPointIndex = -1
                        },
                        onDragCancel = {
                            isTouching = false
                            selectedPointIndex = -1
                        },
                        onDrag = { change, _ ->
                            touchPosition = change.position
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isTouching = true
                            touchPosition = offset
                            awaitRelease()
                            isTouching = false
                            selectedPointIndex = -1
                        }
                    )
                }
                .drawWithCache {
                    val width = size.width
                    val height = size.height - 30f // Reserve space for labels at bottom

                    // Chart dimensions with extra padding for labels that extend outside
                    val padding = 24f
                    val labelPadding = 30f // Extra padding for labels at edges
                    val chartWidth = width - 2 * padding - 2 * labelPadding
                    val chartHeight = height - 2 * padding

                    // Calculate scaling
                    val minValue = 0f
                    val maxValue = processedData.maxOfOrNull { it.second }?.times(1.1f) ?: 1000f
                    val valueRange = maxValue - minValue

                    // Calculate points
                    val xStep = chartWidth / (processedData.size - 1).coerceAtLeast(1)
                    val points = processedData.mapIndexed { index, (_, value) ->
                        val x = padding + labelPadding + index * xStep
                        val y = height - padding - (value / maxValue) * chartHeight
                        Offset(x, y)
                    }

                    // Create smooth path
                    val path = Path()

                    if (points.isNotEmpty()) {
                        path.moveTo(points[0].x, points[0].y)

                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val current = points[i]

                            val ctrl1x = prev.x + (current.x - prev.x) / 3
                            val ctrl1y = prev.y
                            val ctrl2x = current.x - (current.x - prev.x) / 3
                            val ctrl2y = current.y

                            path.cubicTo(
                                ctrl1x, ctrl1y,
                                ctrl2x, ctrl2y,
                                current.x, current.y
                            )
                        }
                    }

                    onDrawBehind {
                        // Fill area under curve
                        if (points.isNotEmpty()) {
                            val fillPath = Path().apply {
                                moveTo(points.first().x, height - padding)
                                lineTo(points.first().x, points.first().y)
                                addPath(path)
                                lineTo(points.last().x, height - padding)
                                lineTo(points.first().x, height - padding)
                                close()
                            }

                            val fillGradient = Brush.verticalGradient(
                                colors = listOf(
                                    chartColor.copy(alpha = 0.4f),
                                    chartColor.copy(alpha = 0.0f)
                                ),
                                startY = points.minOf { it.y },
                                endY = height - padding
                            )

                            drawPath(
                                path = fillPath,
                                brush = fillGradient
                            )
                        }

                        // Draw line chart
                        drawPath(
                            path = path,
                            color = chartColor,
                            style = Stroke(
                                width = 3f,
                                cap = StrokeCap.Round
                            )
                        )

                        // Draw x-axis labels
                        processedData.forEachIndexed { index, (label, _) ->
                            val x = padding + labelPadding + index * xStep
                            val textResult = textMeasurer.measure(label, style = labelStyle)
                            val textWidth = textResult.size.width

                            val textX = x - textWidth / 2
                            val textY = height - padding + 8

                            drawText(
                                textMeasurer = textMeasurer,
                                text = label,
                                topLeft = Offset(textX, textY),
                                style = labelStyle
                            )
                        }

                        // In the onDrawBehind block where we draw the first and last values:
                        if (points.size >= 2) {
                            // First point value (below) - align with left edge
                            val firstPoint = points.first()
                            val firstValue = processedData.first().second
                            val firstValueText = "${firstValue.toInt()} $unit"
                            val firstTextResult = textMeasurer.measure(firstValueText, style = titleStyle)

                            // Left-align with first point
                            val firstTextX = firstPoint.x
                            val firstTextY = firstPoint.y + 12f

                            drawText(
                                textMeasurer = textMeasurer,
                                text = firstValueText,
                                topLeft = Offset(firstTextX, firstTextY),
                                style = titleStyle
                            )

                            // Last point value (above) - align with right edge
                            val lastPoint = points.last()
                            val lastValue = processedData.last().second
                            val lastValueText = "${lastValue.toInt()} $unit"
                            val lastTextResult = textMeasurer.measure(lastValueText, style = titleStyle)

                            // Right-align with last point
                            val lastTextX = lastPoint.x - lastTextResult.size.width
                            val lastTextY = lastPoint.y - lastTextResult.size.height - 12f

                            drawText(
                                textMeasurer = textMeasurer,
                                text = lastValueText,
                                topLeft = Offset(lastTextX, lastTextY),
                                style = titleStyle
                            )
                        }

                        // Find closest point to touch position and show bubble
                        if (isTouching) {
                            // Find closest point
                            var minDistance = Float.MAX_VALUE
                            var closestIndex = -1

                            points.forEachIndexed { index, point ->
                                val distance = (point.x - touchPosition.x).pow(2) +
                                        (point.y - touchPosition.y).pow(2)
                                if (distance < minDistance) {
                                    minDistance = distance
                                    closestIndex = index
                                }
                            }

                            if (closestIndex >= 0) {
                                selectedPointIndex = closestIndex
                                val point = points[closestIndex]
                                val value = processedData[closestIndex].second
                                val label = processedData[closestIndex].first

                                // Draw indicator dot
                                drawCircle(
                                    color = chartColor,
                                    radius = 8f,
                                    center = point
                                )

                                // Draw value bubble
                                val bubbleText = "${value.toInt()} $unit"
                                val bubbleTextResult = textMeasurer.measure(
                                    bubbleText,
                                    style = labelStyle
                                )
                                val bubblePadding = 8.dp.toPx()
                                val bubbleWidth = bubbleTextResult.size.width + bubblePadding * 2
                                val bubbleHeight = bubbleTextResult.size.height + bubblePadding

                                // Bubble position (above the point) with boundary checks
                                var bubbleX = point.x - bubbleWidth / 2
                                val bubbleY = point.y - bubbleHeight - 12f

                                // Ensure bubble stays within chart boundaries
                                if (bubbleX + bubbleWidth > size.width) {
                                    bubbleX = size.width - bubbleWidth - 4f // Add small margin
                                }
                                if (bubbleX < 0) {
                                    bubbleX = 4f // Add small margin
                                }

                                // Draw bubble
                                drawRoundRect(
                                    color = surface,
                                    topLeft = Offset(bubbleX, bubbleY),
                                    size = androidx.compose.ui.geometry.Size(bubbleWidth, bubbleHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
                                )

                                // Draw bubble outline
                                drawRoundRect(
                                    color = chartColor,
                                    topLeft = Offset(bubbleX, bubbleY),
                                    size = androidx.compose.ui.geometry.Size(bubbleWidth, bubbleHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
                                    style = Stroke(width = 1.5f)
                                )

                                // Draw bubble text
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = bubbleText,
                                    topLeft = Offset(
                                        bubbleX + bubblePadding,
                                        bubbleY + bubblePadding / 2
                                    ),
                                    style = labelStyle
                                )
                            }
                        }
                    }
                }
        )
    }
}

@Preview
@Composable
fun VehicleMileageChartPreview() {
    AppTheme {
        Surface {
            VehicleMileageChart(
                modifier = Modifier.size(400.dp),
                data = listOf(
                    "Jan" to 1200f,
                    "Feb" to 1500f,
                    "Mar" to 1800f,
                    "Apr" to 2000f,
                    "May" to 2100f,
                    "Jun" to 2200f,
                    "Jul" to 2300f,
                    "Aug" to 2500f,
                    "Sep" to 2600f,
                    "Oct" to 2700f,
                    "Nov" to 2900f,
                    "Dec" to 3000f
                )
            )
        }
    }
}