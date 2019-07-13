package com.example.desserts.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.desserts.R
import com.example.desserts.api.ApiService
import com.example.desserts.view.questions.QuestionsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_insight_chart.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {

            compositeDisposable.add(
                ApiService.requestLogin()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it?.let {
                            loadQuestions(it.permission)
                        }
                    })
        }
    }

    private fun loadQuestions(isPermitted: Boolean) {
        if (isPermitted) {
            val intent = Intent(this, QuestionsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
        }
    }
}
