package com.example.desserts.api

import com.example.desserts.model.QuestionResponseModel
import com.example.desserts.model.LoginModel
import com.example.desserts.model.QuestionModel
import com.example.desserts.model.WeekModel
import io.reactivex.Observable
import retrofit2.http.*

class ApiService {

    interface ApiImpl {
        @POST("/session")
        fun requestLogin(): Observable<LoginModel>

        @GET("/questions")
        fun requestQuestions(): Observable<List<QuestionModel>>

        @Headers("Accept: application/json")
        @POST("/results")
        fun requestAnswerQuestions(@Body data: HashMap<String, Any>): Observable<QuestionResponseModel>

        @GET("/results/week")
        fun requestWeekData(@Query("date") date: String): Observable<WeekModel>
    }

    companion object {
        fun requestLogin(): Observable<LoginModel> {
            return ApiClient.create(ApiImpl::class.java).requestLogin()
        }

        fun requestQuestions(): Observable<List<QuestionModel>> {
            return ApiClient.create(ApiImpl::class.java).requestQuestions()
        }

        fun requestAnswerQuestions(data: HashMap<String, Any>): Observable<QuestionResponseModel> {
            return ApiClient.create(ApiImpl::class.java).requestAnswerQuestions(data)
        }

        fun requestWeekData(data: String): Observable<WeekModel> {
            return ApiClient.create(ApiImpl::class.java).requestWeekData(data)
        }
    }
}