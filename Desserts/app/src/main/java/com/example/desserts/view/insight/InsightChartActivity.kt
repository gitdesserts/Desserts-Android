package com.example.desserts.view.insight

import android.Manifest
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
//import com.example.desserts.common.GlideApp
import com.example.desserts.R
import com.example.desserts.api.ApiService

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_insight_chart.*
import kotlinx.android.synthetic.main.view_test.view.*
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat
import java.util.*

class InsightChartActivity : AppCompatActivity() {

    private var xAxis = XAxis()

    private var yAxis = YAxis()

    private var xAxisData = arrayOf("월", "화", "수", "목", "금", "토", "일")

    private val compositeDisposable = CompositeDisposable()
    private var year = 0
    private var month = 0

    private var lastWeek = false

    private val calendar = Calendar.getInstance()
    private var timer = Timer()

    private var permission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    inner class CustomTimer: TimerTask() {
        override fun run() {
            runOnUiThread {
                loadingProgressView.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight_chart)

        ActivityCompat.requestPermissions(this, permission, 7979)

        calendar.time = Date()

        requestChartData(getCurrentDate(0))
        Glide.with(this).load(R.drawable.q_loading).into(loadingImageView)
        // Insight
        compositeDisposable.add(
            ApiService.requestInsight(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    insightResultTextView.text = Html.fromHtml(it.content)
                    Glide.with(insightImageView).load(it.img).into(insightImageView)

                }, { error ->
                    print(error.localizedMessage)
                })
        )

        setDatesForMonthlyReport(CalendarDay.today())
        calendarView.setOnMonthChangedListener { materialCalendarView, calendarDay ->
            setDatesForMonthlyReport(calendarDay)
        }

        detailImageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, MonthlyReportFragment()).commit()
            timer = Timer()
            loadingProgressView.visibility = View.VISIBLE
            timer.schedule(CustomTimer(), 2000)
        }

        timer.schedule(CustomTimer(), 2000)
    }

    private fun getCurrentDate(intValue: Int): String {
        val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd")

        calendar.add(Calendar.DATE, intValue)

        return simpleDateFormatter.format(calendar.time)
    }

    private fun requestChartData(date: String) {
        compositeDisposable.add(
            ApiService.requestWeekData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView.text = "${it.month}월 ${convertWeekString(it.week)}"

                    setChart(it.result)

                    previousWeekButton.setOnClickListener {
                        requestChartData(getCurrentDate(-7))
                        lineChartWeek.notifyDataSetChanged()

                        lastWeek = false
                    }

                    nextWeekButton.setOnClickListener {
                        if(!lastWeek) {
                            requestChartData(getCurrentDate(7))
                            lineChartWeek.notifyDataSetChanged()
                        }
                    }

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
    }

    private fun setDatesForMonthlyReport(calendarDay: CalendarDay) {
        val calendar = Calendar.getInstance()
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

    private fun generateLineData(scoreList: ArrayList<Int>): LineData {
        var entries = ArrayList<Entry>()
        for(i in 6 downTo 0) {
            if(scoreList[i] != -1) {
                entries.add((Entry((6-i).toFloat(), scoreList[i].toFloat())))
            }
            else {
                lastWeek = true
            }
        }

        var lineDataSet = LineDataSet(entries, "Line DataSet")
        lineDataSet.color = ContextCompat.getColor(this, R.color.chartLineColor)
        lineDataSet.lineWidth = 1.6f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.chartLineCircleForegroundColor))
        lineDataSet.circleRadius = 4f
        lineDataSet.circleHoleColor = ContextCompat.getColor(this, R.color.chartLineCircleInnerColor)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
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
