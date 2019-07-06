package com.example.desserts.view.insight

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.desserts.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_insight_chart.*

class InsightChartActivity : AppCompatActivity() {

    private val lineChartWeekDataList: List<Entry>? = null

    private val weekAxis: ArrayList<BarEntry>? = null

    private var xAxis = XAxis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight_chart)

        // main chart option
        lineChartWeek.description.isEnabled = false
        lineChartWeek.setBackgroundColor(Color.WHITE)
        lineChartWeek.setDrawGridBackground(false)
        lineChartWeek.setDrawBarShadow(false)
        lineChartWeek.isHighlightFullBarEnabled = false

        // left option
        var leftAxis = lineChartWeek.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f

        // bottom option
        xAxis = lineChartWeek.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(getXAxisValues())

        // lineChart and barChart - combine
        var combinedData = CombinedData()

        combinedData.setData(generateLineData())
        combinedData.setData(generateBarData())

        lineChartWeek.data = combinedData
        lineChartWeek.invalidate()


        // Calendar
//        calendarView.calendarOrientation = LinearLayout.HORIZONTAL
    }

    private fun generateLineData(): LineData {
        var entries = ArrayList<Entry>()
        entries.add(Entry(00f, 3f))
        entries.add(Entry(10f, 2f))
        entries.add(Entry(20f, 5f))
        entries.add(Entry(30f, 1f))
        entries.add(Entry(40f, 8f))
        entries.add(Entry(50f, 4f))
        entries.add(Entry(60f, 9f))

        var lineDataSet = LineDataSet(entries, "Line DataSet")
        lineDataSet.color = Color.BLACK
        lineDataSet.lineWidth = 2.5f
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.circleRadius = 5f
        lineDataSet.fillColor = Color.WHITE
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.setDrawValues(false)

        var lineData = LineData()

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

        lineData.addDataSet(lineDataSet)

        return lineData
    }

    private fun generateBarData(): BarData {
        var entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 3f))
        entries.add(BarEntry(10f, 2f))
        entries.add(BarEntry(20f, 5f))
        entries.add(BarEntry(30f, 1f))
        entries.add(BarEntry(40f, 8f))
        entries.add(BarEntry(50f, 4f))
        entries.add(BarEntry(60f, 9f))

        var barDataSet = BarDataSet(entries, "Bar Data")
        barDataSet.color = Color.RED
        barDataSet.setDrawValues(false)
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT

        var barWidth = 5f

        var barData = BarData(barDataSet)
        barData.barWidth = barWidth

        return barData
    }

    private fun getXAxisValues(): ArrayList<String> {
        var xAxis = ArrayList<String>()
        xAxis.add("일")
        xAxis.add("월")
        xAxis.add("화")
        xAxis.add("수")
        xAxis.add("목")
        xAxis.add("금")
        xAxis.add("토")

        return xAxis
    }


}
