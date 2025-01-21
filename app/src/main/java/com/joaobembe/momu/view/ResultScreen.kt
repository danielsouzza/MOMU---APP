package com.joaobembe.momu.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.joaobembe.momu.data.models.ResultResponse
import com.joaobembe.momu.viewmodel.AssessmentDetailState
import com.joaobembe.momu.viewmodel.AssessmentViewModel
import network.chaintech.cmpcharts.axis.AxisConfiguration
import network.chaintech.cmpcharts.axis.AxisProperties
import network.chaintech.cmpcharts.axis.DataCategorySettings
import network.chaintech.cmpcharts.common.model.Point
import network.chaintech.cmpcharts.ui.barchart.BarChart
import network.chaintech.cmpcharts.ui.barchart.config.BarChartConfig
import network.chaintech.cmpcharts.ui.barchart.config.BarChartStyle
import network.chaintech.cmpcharts.ui.barchart.config.BarChartType
import network.chaintech.cmpcharts.ui.barchart.config.BarData
import network.chaintech.cmpcharts.ui.radarchart.RadarChart
import network.chaintech.cmpcharts.ui.radarchart.config.RadarChartConfig
import network.chaintech.cmpcharts.ui.radarchart.config.RadarChartDataSet
import network.chaintech.cmpcharts.ui.radarchart.config.RadarChartDataSetStyle
import network.chaintech.cmpcharts.ui.radarchart.config.RadarChartLineStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    assessmentId: Int,
    viewModel: AssessmentViewModel
) {
    val assessmentDetailState by remember { viewModel.assessmentDetailState }.collectAsState()

    val assessmentIdState = rememberSaveable { mutableIntStateOf(assessmentId) }
    LaunchedEffect(assessmentIdState.intValue) {
        viewModel.fetchResultAssessment(assessmentIdState.intValue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado da avaliação") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopStart
        ) {
            Log.d("UI", "Recomposing Box")
            when (val state = assessmentDetailState) {
                is AssessmentDetailState.Loading -> {
                    LoadingScreen()
                }

                is AssessmentDetailState.Success -> {
                    state.assessment?.let { result ->
                        SuccessScreen(result)
                    }
                }

                is AssessmentDetailState.Error -> {
                    ErrorScreen("Erro ao carregar detalhes") { }
                }
            }
        }
    }
}

fun getBarChartData(
    barChartType: BarChartType,
    barSizeList: List<Double>,
    labelList: List<String>
): List<BarData> {
    val list = arrayListOf<BarData>()
    for (index in barSizeList.indices) {
        val point = when (barChartType) {
            BarChartType.VERTICAL -> {
                Point(
                    index.toFloat(),
                    barSizeList[index].toFloat()
                )
            }

            BarChartType.HORIZONTAL -> {
                Point(
                    barSizeList[index].toFloat(),
                    index.toFloat()
                )
            }
        }

        list.add(
            BarData(
                point = point,
                color = Color.Blue,
                label = labelList[index],
            )
        )
    }
    return list
}

@Composable
fun ErrorScreen(message: String?, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Erro: ${message ?: "Erro desconhecido"}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Voltar")
        }
    }
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator()
}

@Composable
fun SuccessScreen(assessment: ResultResponse?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        assessment?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Avaliação por dimensão",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 8.dp)
                )
                RadarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    radarChartConfig = RadarChartConfig(
                        sideLength = it.chart.labels.size,
                        maxLabelWidth = 25.dp,
                        scaleStepsCount = 5,
                        radarChartDataSet = listOf(
                            RadarChartDataSet(
                                style = RadarChartDataSetStyle(
                                    color = Color(0xFF6495ED),
                                    borderColor = Color(0xFF00008B),
                                    colorAlpha = 0.1f,
                                    strokeCap = StrokeCap.Butt,
                                    strokeWidth = 5.0f,
                                ), data = it.chart.scores
                            ),
                        ),
                        radarChartLineStyle = RadarChartLineStyle(
                            color = Color.Gray,
                            strokeCap = StrokeCap.Butt,
                            strokeWidth = 1f
                        ),
                        scaleTextStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 11.sp,

                            ),
                        labelTextStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 12.sp,

                            ),
                        maxScaleValue = 100.0,
                        scaleUnit = "",
                        radarSegmentsLabels = it.chart.labels,
                    )
                )
            }


            val barData = getBarChartData(
                BarChartType.HORIZONTAL,
                it.chart.scores,
                it.chart.labels
            )

            val yAxisData = AxisProperties(
                stepSize = 30.dp,
                stepCount = barData.size,
                labelPadding = 10.dp,
                offset = 10.dp,
                labelColor = Color.Black,
                lineColor = Color.Transparent,
                categoryOptions = DataCategorySettings(
                    isYAxisCategory = true,
                    startsFromBottom = false
                ),
                initialDrawPadding = 16.dp,
                labelFormatter = { index ->
                    barData[index].label
                },
            )

            val xAxisData = AxisProperties(
                stepCount = 10,
                labelColor = Color.Black,
                lineColor = Color.Transparent,
                config = AxisConfiguration(
                    isLineVisible = true,
                )
            )


            val barChartData = BarChartConfig(
                chartData = barData,
                yAxisData = yAxisData,
                xAxisData = xAxisData,
                backgroundColor = Color.Transparent,
                horizontalExtraSpace = 20.dp,
                barChartType = BarChartType.HORIZONTAL,
                barStyle = BarChartStyle(
                    barWidth = 35.dp,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Desenvolvimento do nível de maturidade",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                BarChart(
                    modifier = Modifier
                        .height(460.dp),
                    barChartData = barChartData
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        } ?: run {
            Text(text = "Nenhum resultado disponível")
        }
    }
}