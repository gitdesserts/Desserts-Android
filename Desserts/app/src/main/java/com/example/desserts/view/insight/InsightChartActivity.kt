package com.example.desserts.view.insight

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import java.util.*
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat


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
        setChart()

        requestChartData()

    }

    private fun requestChartData() {
        compositeDisposable.add(
            ApiService.requestWeekData("2019-07-06")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    print(it.month)
                }, { error ->
                    print(error.localizedMessage)
                })
        )
    }

    private fun setChart() {
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

        // right option
        var rightAxis = lineChartWeek.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)

        yAxis = lineChartWeek.getAxis(YAxis.AxisDependency.LEFT)
        yAxis.granularity = 1f
        yAxis.axisMaximum = 10f
        yAxis.labelCount = 10

        // bottom option
        xAxis = lineChartWeek.xAxis
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

        combinedData.setData(generateLineData())
        combinedData.setData(generateBarData())

        lineChartWeek.setVisibleXRangeMaximum(7f)
        lineChartWeek.setTouchEnabled(false)
        lineChartWeek.isHighlightPerTapEnabled = false
        lineChartWeek.legend.isEnabled = false

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

    private fun generateLineData(): LineData {
        var entries = ArrayList<Entry>()
        entries.add(Entry(0f, 3f))
        entries.add(Entry(1f, 2f))
        entries.add(Entry(2f, 5f))
        entries.add(Entry(3f, 1f))
        entries.add(Entry(4f, 8f))
        entries.add(Entry(5f, 4f))
        entries.add(Entry(6f, 9f))

        var lineDataSet = LineDataSet(entries, "Line DataSet")
        lineDataSet.color = Color.BLACK
        lineDataSet.lineWidth = 1.6f
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.circleRadius = 4f
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
        entries.add(BarEntry(1f, 2f))
        entries.add(BarEntry(2f, 5f))
        entries.add(BarEntry(3f, 1f))
        entries.add(BarEntry(4f, 8f))
        entries.add(BarEntry(5f, 4f))
        entries.add(BarEntry(6f, 9f))

        var barDataSet = BarDataSet(entries, "Bar Data")
        barDataSet.color = Color.RED
        barDataSet.setDrawValues(false)
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT

        var barWidth = 0.7f

        var barData = BarData(barDataSet)
        barData.barWidth = barWidth

        return barData
    }
}
