package com.example.desserts.view.questions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.desserts.model.QuestionModel
import com.example.desserts.view.BaseFragment

class QuestionPageAdapter : FragmentPagerAdapter {

    private val pageList: MutableList<BaseFragment> = mutableListOf()

    constructor(fragmentManager: FragmentManager, list: List<QuestionModel>) : super(fragmentManager) {
        for (i in 0 until list.size) {
            pageList.add(QuestionFragment.newInstance(list[i].content))
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageList[position].title()
    }

    override fun getItem(position: Int): Fragment {
        return pageList.get(position)
    }

    override fun getCount(): Int {
        return pageList.size
    }

}