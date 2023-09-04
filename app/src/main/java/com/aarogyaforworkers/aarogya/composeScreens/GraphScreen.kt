package com.aarogyaforworkers.aarogya.composeScreens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import com.aarogyaforworkers.aarogya.Commons.selectedECGResult
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.random.Random

var isClosing = false


@ExperimentalTvMaterial3Api
@Composable
fun GraphScreen(navHostController: NavHostController, data : ArrayList<Float>) {
    // Get the Context from LocalContext
    val context = LocalContext.current

    // Set the screen orientation to landscape
    SideEffect {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            IconButton(onClick = {
                isClosing = true
                navHostController.navigate(Destination.SessionHistory.routes)
            }) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "")
            }
            Spacer(modifier = Modifier.width(25.dp))
            val result = context.resources.getStringArray(R.array.ecg_measureres)[selectedECGResult]
            if(!result.isNullOrEmpty() && result != "55") {
                Box(
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                ) {
                    BoldTextView(title = result)
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()){
                GraphView(data = data, title = "Random Data")
            }
        }
    }

    
}



@Preview
@Composable
fun RandomDataGraphPreview() {

    val randomData = List(2000) { Random.nextFloat() }
    GraphView(data = randomData, title = "Random Data")

//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { context ->
//            val graphView = GraphView(context)
//            val randomData = (0..2000).map { Random.nextFloat() }
//            val series = LineGraphSeries(randomData.mapIndexed { index, value -> DataPoint(index.toDouble(), value.toDouble()) }.toTypedArray())
//            graphView.addSeries(series)
//            graphView
//        }
//    )
}



@Composable
fun GraphView(data: List<Float>, title: String) {

    val cellSize = 10.dp
    val strokeWidth = with(LocalDensity.current) { 0.2.dp.toPx() }
    val lightPink = Color(android.graphics.Color.parseColor("#FFC0CB")).copy(alpha = 0.2f)


    val dataSet = remember {
        LineDataSet(data.mapIndexed { index, value ->
            Entry((index + 1.5).toFloat(), value) },
            title)
    }

    dataSet.color = Color.Black.hashCode()
    dataSet.setDrawCircles(false)
    dataSet.setDrawValues(false)

    val lineData = LineData(dataSet)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = lightPink)
    ) {
        val columns = (size.width / cellSize.toPx()).toInt()
        val rows = (size.height / cellSize.toPx()).toInt()

        for (i in 0 until columns) {
            for (j in 0 until rows) {
                val startX = i * cellSize.toPx()
                val startY = j * cellSize.toPx()

                drawRect(
                    color = Color.Red,
                    topLeft = Offset(startX, startY),
                    size = Size(cellSize.toPx(), cellSize.toPx()),
                    style = Stroke(strokeWidth)
                )
            }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val lineChart = LineChart(context)
            lineChart.setBackgroundColor(Color.Transparent.hashCode())
            lineChart.data = lineData
            lineChart.setBorderColor(Color.Transparent.hashCode())
            lineChart.setTouchEnabled(true)
            lineChart.isDragEnabled = true
            lineChart.setScaleEnabled(true)
            lineChart.setPinchZoom(true)
            lineChart.setDrawGridBackground(false)
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.axisLeft.setDrawLabels(false)
            lineChart.axisRight.setDrawLabels(false)
            lineChart.axisLeft.isInverted = true
            lineChart.axisRight.isInverted = true
            lineChart.xAxis.setDrawLabels(false)
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisRight.setDrawGridLines(false)
            lineChart.xAxis.setDrawGridLines(false)
            lineChart.setVisibleXRangeMaximum(500f)
            lineChart.moveViewToX(0f)
            lineChart.zoom(0f, 1f, 0f, 0f)
            lineChart.invalidate()
            lineChart
        }
    )
}




fun downloadCsvDataFromUrl(url: String, completion: (List<Double>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
            // Handle download failure
            Log.d("TAG", "onFailure: ")
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            // Check if response is successful
            if (response.isSuccessful) {
                // Parse the response body into a list of Double values
                val csvData = response.body?.string() ?: ""
                val data = parseCsvData(csvData)
                // Call completion block with parsed data
                completion(data)
            }
        }
    })
}

// Helper function to parse CSV data into a list of Double values
fun parseCsvData(csvData: String): List<Double> {
    val lines = csvData.trim().split("\n")
    val data = mutableListOf<Double>()
    for (line in lines) {
        val values = line.split(",").map { it.trim() }
        for (value in values) {
            data.add(value.toDoubleOrNull() ?: 0.0)
        }
    }
    return data
}


