package com.example.desserts.view.insight


import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.desserts.R
import com.example.desserts.api.ApiService
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_monthly_report.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Bitmap
import android.os.Environment.getExternalStorageDirectory
import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import android.view.ViewTreeObserver
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 *
 */
class MonthlyReportFragment : androidx.fragment.app.Fragment() {

    private var mContext: Context? = null

    private val compositeDisposable = CompositeDisposable()

    private var xAxisData = arrayOf("월", "화", "수", "목", "금", "토", "일")

    private val calendar = Calendar.getInstance()

    private var b: Bitmap? = null

    private var uri: Uri? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monthly_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setLine1(getCurrentDate(0))
        setLine2(getCurrentDate(-7))
        setLine3(getCurrentDate(-7))
        setLine4(getCurrentDate(-7))

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("image/png")
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri.toString()))
            intent.setPackage("com.kakao.talk")
            startActivity(intent)
        }

    }

    private fun getCurrentDate(intValue: Int): String {
        val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd")

        calendar.add(Calendar.DATE, intValue)

        return simpleDateFormatter.format(calendar.time)
    }

    private fun setLine1(date: String) {
        compositeDisposable.add(
            ApiService.requestWeekData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView.text = "${it.month}월 ${convertWeekString(it.week)}"

                    var lineData1 = generateLineData(it.result)

                    line1.description.isEnabled = false
                    line1.setDrawGridBackground(false)

                    // left option
                    var leftAxis = line1.axisLeft
                    leftAxis.setDrawGridLines(false)
                    leftAxis.axisMinimum = 0f

                    // right option
                    var rightAxis = line1.axisRight
                    rightAxis.setDrawLabels(false)
                    rightAxis.setDrawAxisLine(false)
                    rightAxis.setDrawGridLines(false)

                    var yAxis = line1.getAxis(YAxis.AxisDependency.LEFT)
                    yAxis.granularity = 1f
                    yAxis.axisMaximum = 10f
                    yAxis.labelCount = 10
                    yAxis.setDrawAxisLine(false)
                    yAxis.setDrawLabels(false)

                    // bottom option
                    var xAxis = line1.xAxis
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelCount = 7
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return xAxisData[value.toInt() % xAxisData.size]
                        }
                    }

                    line1.setTouchEnabled(false)
                    line1.legend.isEnabled = false
                    line1.setBackgroundResource(R.drawable.gradient)
                    line1.alpha = 0.5f

                    line1.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            line1.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            var b = Bitmap.createBitmap(line1.measuredWidth, line1.measuredHeight, Bitmap.Config.ARGB_8888)

                            var c = Canvas(b)
                            line1.layout(line1.left, line1.top, line1.right, line1.bottom)
                            line1.draw(c)

                            var fileName = "chartImage.png"
                            var sd = Environment.getExternalStorageDirectory()
                            var dest = File(sd, fileName)

                            try {
                                var out = FileOutputStream(dest)
                                b.compress(Bitmap.CompressFormat.PNG, 90, out)
                                out.flush()
                                out.close()

                                uri = FileProvider.getUriForFile(mContext!!.applicationContext, mContext!!.applicationContext.packageName + ".provider", dest)
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })

                    line1.data = lineData1
                    line1.invalidate()

                }, { error ->
                    print(error.localizedMessage)
                })
        )
    }

    private fun setLine2(date: String) {
        compositeDisposable.add(
            ApiService.requestWeekData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView2.text = "${it.month}월 ${convertWeekString(it.week)}"

                    var lineData1 = generateLineData(it.result)

                    line2.description.isEnabled = false
                    line2.setDrawGridBackground(false)

                    // left option
                    var leftAxis = line2.axisLeft
                    leftAxis.setDrawGridLines(false)
                    leftAxis.axisMinimum = 0f

                    // right option
                    var rightAxis = line2.axisRight
                    rightAxis.setDrawLabels(false)
                    rightAxis.setDrawAxisLine(false)
                    rightAxis.setDrawGridLines(false)

                    var yAxis = line2.getAxis(YAxis.AxisDependency.LEFT)
                    yAxis.granularity = 1f
                    yAxis.axisMaximum = 10f
                    yAxis.labelCount = 10
                    yAxis.setDrawAxisLine(false)
                    yAxis.setDrawLabels(false)

                    // bottom option
                    var xAxis = line2.xAxis
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelCount = 7
                    xAxis.isEnabled = false

                    line2.setTouchEnabled(false)
                    line2.legend.isEnabled = false
                    line2.setBackgroundResource(R.drawable.gradient2)
                    line2.alpha = 0.5f

                    line2.data = lineData1
                    line2.invalidate()

                }, { error ->
                    print(error.localizedMessage)
                })
        )
    }

    private fun setLine3(date: String) {
        compositeDisposable.add(
            ApiService.requestWeekData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView3.text = "${it.month}월 ${convertWeekString(it.week)}"

                    var lineData1 = generateLineData(it.result)

                    line3.description.isEnabled = false
                    line3.setDrawGridBackground(false)

                    // left option
                    var leftAxis = line3.axisLeft
                    leftAxis.setDrawGridLines(false)
                    leftAxis.axisMinimum = 0f

                    // right option
                    var rightAxis = line3.axisRight
                    rightAxis.setDrawLabels(false)
                    rightAxis.setDrawAxisLine(false)
                    rightAxis.setDrawGridLines(false)

                    var yAxis = line3.getAxis(YAxis.AxisDependency.LEFT)
                    yAxis.granularity = 1f
                    yAxis.axisMaximum = 10f
                    yAxis.labelCount = 10
                    yAxis.setDrawAxisLine(false)
                    yAxis.setDrawLabels(false)

                    // bottom option
                    var xAxis = line3.xAxis
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelCount = 7
                    xAxis.isEnabled = false

                    line3.setTouchEnabled(false)
                    line3.legend.isEnabled = false
                    line3.setBackgroundResource(R.drawable.gradient3)
                    line3.alpha = 0.5f

                    line3.data = lineData1
                    line3.invalidate()

                }, { error ->
                    print(error.localizedMessage)
                })
        )
    }

    private fun setLine4(date: String) {
        compositeDisposable.add(
            ApiService.requestWeekData(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    weekTextView4.text = "${it.month}월 ${convertWeekString(it.week)}"

                    var lineData1 = generateLineData(it.result)

                    line4.description.isEnabled = false
                    line4.setDrawGridBackground(false)

                    // left option
                    var leftAxis = line4.axisLeft
                    leftAxis.setDrawGridLines(false)
                    leftAxis.axisMinimum = 0f

                    // right option
                    var rightAxis = line4.axisRight
                    rightAxis.setDrawLabels(false)
                    rightAxis.setDrawAxisLine(false)
                    rightAxis.setDrawGridLines(false)

                    var yAxis = line4.getAxis(YAxis.AxisDependency.LEFT)
                    yAxis.granularity = 1f
                    yAxis.axisMaximum = 10f
                    yAxis.labelCount = 10
                    yAxis.setDrawAxisLine(false)
                    yAxis.setDrawLabels(false)

                    // bottom option
                    var xAxis = line4.xAxis
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawGridLines(false)
                    xAxis.granularity = 1f
                    xAxis.labelCount = 7
                    xAxis.isEnabled = false

                    line4.setTouchEnabled(false)
                    line4.legend.isEnabled = false
                    line4.setBackgroundResource(R.drawable.gradient4)
                    line4.alpha = 0.5f

                    line4.data = lineData1
                    line4.invalidate()

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
        }

        var lineDataSet = LineDataSet(entries, "Line DataSet")
        lineDataSet.color = Color.BLACK
        lineDataSet.lineWidth = 1.6f
        lineDataSet.setCircleColor(ContextCompat.getColor(mContext!!, R.color.chartLineMonthlyReportCircle))
        lineDataSet.circleRadius = 2f
        lineDataSet.circleHoleColor = ContextCompat.getColor(mContext!!, R.color.chartLineMonthlyReportCircle)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.setDrawValues(false)

        var lineData = LineData()

        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

        lineData.addDataSet(lineDataSet)

        return lineData
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
}
