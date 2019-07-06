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
    }
}
