package com.example.desserts.view.insight

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.desserts.R
import com.example.desserts.api.ApiService
import com.example.desserts.common.GlideApp
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_insight_chart.*
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class InsightChartActivity : AppCompatActivity() {

    private var xAxis = XAxis()

    private var yAxis = YAxis()

    private var xAxisData = arrayOf("월", "화", "수", "목", "금", "토", "일")

    private val compositeDisposable = CompositeDisposable()
    private var year = 0
    private var month = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight_chart)

        // set chart
//        setChart()

        requestChartData()

    }

    private fun getCurrentDate(): String {
        val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd")

        return simpleDateFormatter.format(Date())
    }

    private fun requestChartData() {
        compositeDisposable.add(
            ApiService.requestWeekData(getCurrentDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView.text = "${it.month}월 ${convertWeekString(it.week)}"

                    setChart(it.result)
//                    print(it.month)
                }, { error ->
                    print(error.localizedMessage)
                })
        )
    }

    private fun convertWeekString(weekString: Int): String {
        return when(weekString) {
            1 -> { "첫째주" }

            2 -> { "둘째주" }

            3 -> { "셋째주" }

            4 -> { "넷째주" }

            5 -> { "다섯째주" }

            else -> { "" }
        }
    }

    private fun setChart(scoreList: ArrayList<Int>) {
        // main chart option
        lineChartWeek.description.isEnabled = false
        lineChartWeek.setDrawGridBackground(false)
        lineChartWeek.setDrawBarShadow(false)
        lineChartWeek.isHighlightFullBarEnabled = false

        // left option
        var leftAxis = lineChartWeek.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisLineColor = ContextCompat.getColor(this, R.color.chartGridColor)
        leftAxis.gridColor = ContextCompat.getColor(this, R.color.chartGridColor)
        leftAxis.textColor = ContextCompat.getColor(this, R.color.chartTextColor)
        leftAxis.axisMinimum = 0f

        // right option
        var rightAxis = lineChartWeek.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(true)
        rightAxis.axisLineColor = ContextCompat.getColor(this, R.color.chartGridColor)
        rightAxis.setDrawGridLines(false)

        yAxis = lineChartWeek.getAxis(YAxis.AxisDependency.LEFT)
        yAxis.granularity = 1f
        yAxis.axisMaximum = 10f
        yAxis.labelCount = 10

        // bottom option
        xAxis = lineChartWeek.xAxis
        xAxis.axisLineColor = ContextCompat.getColor(this, R.color.chartGridColor)
        xAxis.gridColor = ContextCompat.getColor(this, R.color.chartGridColor)
        xAxis.textColor = ContextCompat.getColor(this, R.color.chartTextColor)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = 7
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xAxisData[value.toInt() % xAxisData.size]
            }
        }

        // lineChart and barChart - combine
        var combinedData = CombinedData()

        combinedData.setData(generateLineData(scoreList))
        combinedData.setData(generateBarData(scoreList))

        lineChartWeek.setVisibleXRangeMaximum(7f)
        lineChartWeek.setTouchEnabled(false)
        lineChartWeek.isHighlightPerTapEnabled = false
        lineChartWeek.legend.isEnabled = false
        lineChartWeek.setBackgroundColor(Color.TRANSPARENT)

        lineChartWeek.xAxis.axisMinimum = -0.45f
        lineChartWeek.xAxis.axisMaximum = combinedData.xMax + 0.45f

        lineChartWeek.data = combinedData
        lineChartWeek.invalidate()

        // Insight
//        Glide.with(this).load("").thumbnail(0.1f).placeholder(R.drawable.loading_spinner).into(holder3.sendImageVIew2);
//        GlideApp.with(insightImageView).load(userItem.imagePath).fitCenter().placeholder(R.drawable.image_place_holder).into(itemView.selectedUserImageView)

        // Calendar
        val calendar = Calendar.getInstance()

        calendarView.setOnMonthChangedListener { materialCalendarView, calendarDay ->
//            calendarView.clearSelection()
            year = calendarDay.year
            month = calendarDay.month
            calendar.set(year, month - 1, 1)

            val formatted = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
            compositeDisposable.add(
                ApiService.requestMonthData(formatted)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({


                        if (year == it.year && month == it.month) {
//                            for (i in 0 until it.result.size) {
//                                if (it.result[i] > 0) {
//
//                                    calendarView.setDateSelected(CalendarDay.from(year, month, i + 1), true)
//                                }
//                            }

                            val days1: MutableList<CalendarDay> = mutableListOf()
                            val days2: MutableList<CalendarDay> = mutableListOf()
                            val days3: MutableList<CalendarDay> = mutableListOf()
                            val days4: MutableList<CalendarDay> = mutableListOf()

                            it.result.forEachIndexed { index, i ->
                                when (i) {
                                    0, 1 -> {
                                        days1.add(CalendarDay.from(year, month, index + 1))
                                    }
                                    2, 3, 4 -> {
                                        days2.add(CalendarDay.from(year, month, index + 1))
                                    }
                                    5, 6, 7 -> {
                                        days3.add(CalendarDay.from(year, month, index + 1))
                                    }
                                    8, 9, 10 -> {
                                        days4.add(CalendarDay.from(year, month, index + 1))
                                    }
                                }
                            }

                            calendarView.addDecorators(
                                DateDecorator(1, days1, this),
                                DateDecorator(2, days2, this),
                                DateDecorator(3, days3, this),
                                DateDecorator(4, days4, this)
                            )
                        }

                    }, { error ->
                        print(error.localizedMessage)
                    })
            )
        }
    }

    private fun generateLineData(scoreList: ArrayList<Int>): LineData {
        var entries = ArrayList<Entry>()
        for(i in 6 downTo 0) {
            entries.add((Entry((6-i).toFloat(), scoreList[i].toFloat())))
        }

        var lineDataSet = LineDataSet(entries, "Line DataSet")
        lineDataSet.color = ContextCompat.getColor(this, R.color.chartLineColor)
        lineDataSet.lineWidth = 1.6f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.chartLineCircleForegroundColor))
        lineDataSet.circleRadius = 4f
        lineDataSet.circleHoleColor = ContextCompat.getColor(this, R.color.chartLineCircleInnerColor)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.setDrawValues(false)

        var lineData = LineData()

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

        lineData.addDataSet(lineDataSet)

        return lineData
    }

    private fun generateBarData(scoreList: ArrayList<Int>): BarData {
        var entries = ArrayList<BarEntry>()

        for(i in 6 downTo 0) {
            entries.add((BarEntry((6-i).toFloat(), scoreList[i].toFloat())))
        }

        var barDataSet = BarDataSet(entries, "Bar Data")
        barDataSet.color = ContextCompat.getColor(this, R.color.chartBarColor)
        barDataSet.setDrawValues(false)
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT

        var barWidth = 0.55f

        var barData = BarData(barDataSet)
        barData.barWidth = barWidth

        return barData
    }
}
