package com.example.desserts.view.questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.desserts.R
import com.example.desserts.view.BaseFragment
import kotlinx.android.synthetic.main.fragment_question.view.*


class QuestionFragment : BaseFragment() {
    var questionText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(QUESTION_TITLE)?.let { title ->
                questionText = title
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        view.questionTextView.text = questionText

        return view
    }

    override fun title(): String {
        return questionText
    }

    companion object {
        fun newInstance(questionTitle: String): QuestionFragment {
            val fragment = QuestionFragment()

            val args = Bundle()
            args.putString(QUESTION_TITLE, questionTitle)
            fragment.arguments = args
            return fragment
        }

        const val QUESTION_TITLE = "questionTitle"
    }
}