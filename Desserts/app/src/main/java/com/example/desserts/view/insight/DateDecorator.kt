package com.example.desserts.view.insight

import android.content.Context
import com.example.desserts.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DateDecorator: DayViewDecorator {

    var level = 0
    var dates: HashSet<CalendarDay> = hashSetOf()
    var context: Context

    constructor(level: Int, dates: Collection<CalendarDay>, context: Context) : super() {
        this.level = level
        this.dates = dates.toHashSet()
        this.context = context
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        when (level) {
            1 -> {
                view?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.date_circle_1))
            }
            2 -> {
                view?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.date_circle_2))
            }
            3 -> {
                view?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.date_circle_3))
            }
            4 -> {
                view?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.date_circle_4))
            }
        }
    }
}