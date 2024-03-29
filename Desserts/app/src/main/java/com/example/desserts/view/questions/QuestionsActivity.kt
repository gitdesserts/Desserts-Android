package com.example.desserts.view.questions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.desserts.R
import com.example.desserts.api.ApiService
import com.example.desserts.model.QuestionModel
import com.example.desserts.view.insight.InsightChartActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_questions.*
import java.util.*
import kotlin.collections.HashMap
import com.bumptech.glide.Glide

class QuestionsActivity: AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private var questionAnswers: MutableList<Map<String, Int>> = mutableListOf()
    private var questionList: MutableList<QuestionModel> = mutableListOf()

    private var timer = Timer()

    inner class CustomTimer: TimerTask() {
        override fun run() {
            runOnUiThread {
                loadingProgressView.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        var questionCount = 0

        Glide.with(this).load(R.drawable.q_loading).into(loadingImageView)
        timer.schedule(CustomTimer(), 2500)

        compositeDisposable.add(
            ApiService.requestQuestions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        questionCount = it.size
                        questionList = it.toMutableList()
                        initPager()
                    }
                },{ error ->
                    print(error.localizedMessage)
                })
        )

        yesButton.setOnClickListener {
            if (questionViewPager.currentItem < questionCount - 1) {
                val question = questionList[questionViewPager.currentItem]
                questionAnswers.add(makeAnswer(id = question.id, score = 1))
                questionViewPager.currentItem += 1
            } else {
                registerQuestionAnswersAndOpenInsightChart()
            }
        }

        noButton.setOnClickListener {
            if (questionViewPager.currentItem < questionCount - 1) {
                val question = questionList[questionViewPager.currentItem]
                questionAnswers.add(makeAnswer(id = question.id, score = -1))
                questionViewPager.currentItem += 1
            } else {
                registerQuestionAnswersAndOpenInsightChart()
            }
        }

        skipButton.setOnClickListener {
            if (questionViewPager.currentItem < questionCount - 1) {
                val question = questionList[questionViewPager.currentItem]
                questionAnswers.add(makeAnswer(id = question.id, score = 0))
                questionViewPager.currentItem += 1
            } else {
                registerQuestionAnswersAndOpenInsightChart()
            }
        }
    }

    private fun initPager() {
        val adapter = QuestionPageAdapter(supportFragmentManager, questionList)
        questionViewPager.adapter = adapter

        // 페이저 스크롤 막기
        questionViewPager.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }
    }

    private fun makeAnswer(id: Int, score: Int): Map<String, Int> {
        return mutableMapOf(Pair<String, Int>("id", id), Pair<String, Int>("score", score))
    }

    private fun registerQuestionAnswersAndOpenInsightChart() {
        val data: HashMap<String, Any> = hashMapOf()
        data["results"] = questionAnswers.toList()

        compositeDisposable.add(
            ApiService.requestAnswerQuestions(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        val intent = Intent(this, InsightChartActivity::class.java)
                        intent.putExtra("creator", it.creator)
                        startActivity(intent)
                    }
                },{ error ->
                    print(error.localizedMessage)
                })
        )
    }
}
