package com.example.desserts.view.questions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.desserts.R
import kotlinx.android.synthetic.main.activity_questions.*

class QuestionsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        val adapter = QuestionPageAdapter(supportFragmentManager, listOf("First", "Second", "Third"))
        questionViewPager.adapter = adapter

        // 페이저 스크롤 막기
        questionViewPager.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }

        yesButton.setOnClickListener {
            questionViewPager.currentItem = 2
        }

        noButton.setOnClickListener {

        }

        skipButton.setOnClickListener {

        }
    }
}
