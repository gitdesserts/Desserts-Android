package com.example.desserts.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.desserts.R
import com.example.desserts.api.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

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
                            if (it.permission) {
                                // todo -> next activity
                                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                // todo -> 예외
                                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }
}
