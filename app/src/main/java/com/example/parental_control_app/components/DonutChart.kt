package com.example.parental_control_app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.parental_control_app.data.DonutChartDataCollection
import com.example.parental_control_app.data.DrawingAngles
import com.example.parental_control_app.data.STROKE_SIZE_UNSELECTED
import com.example.parental_control_app.data.calculateGapAngle
import com.example.parental_control_app.data.findSweepAngle

@Composable
fun DonutChart(
    data: DonutChartDataCollection,
    modifier: Modifier = Modifier,
    chartSize: Dp = 350.dp,
    gapPercentage: Float = 0.04f,
//    selectionView: @Composable (selectedItem: DonutChartData?) -> Unit = {},
) {
    val anglesList: MutableList<DrawingAngles> = remember { mutableListOf() }
    val gapAngle = data.calculateGapAngle(gapPercentage)

    // 1
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        // 2
        Canvas(
            modifier = Modifier
                .size(chartSize),
            // 3
            onDraw = {
                val defaultStrokeWidth = STROKE_SIZE_UNSELECTED.toPx()
                anglesList.clear()
                var lastAngle = 0f
                data.items.forEachIndexed { ind, item ->
                    val sweepAngle = data.findSweepAngle(ind, gapPercentage)
                    anglesList.add(DrawingAngles(lastAngle, sweepAngle))
                    // 4
                    drawArc(
                        color = item.color,
                        startAngle = lastAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(defaultStrokeWidth / 2, defaultStrokeWidth / 2),
                        style = Stroke(defaultStrokeWidth, cap = StrokeCap.Butt),
                        size = Size(size.width - defaultStrokeWidth,
                            size.height - defaultStrokeWidth)
                    )
                    // 5
                    lastAngle += sweepAngle + gapAngle
                }
            }
        )
    }
}