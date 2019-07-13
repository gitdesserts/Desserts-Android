package com.example.desserts.view.questions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.desserts.model.QuestionModel
import com.example.desserts.view.BaseFragment

class QuestionPageAdapter : androidx.fragment.app.FragmentPagerAdapter {

    private val pageList: MutableList<BaseFragment> = mutableListOf()

    constructor(fragmentManager: androidx.fragment.app.FragmentManager, list: List<QuestionModel>) : super(fragmentManager) {
        for (i in 0 until list.size) {
            pageList.add(QuestionFragment.newInstance(list[i].content))
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageList[position].title()
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return pageList.get(position)
    }

    override fun getCount(): Int {
        return pageList.size
    }

}