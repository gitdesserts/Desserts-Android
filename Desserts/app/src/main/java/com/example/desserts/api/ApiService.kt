package com.example.desserts.api

import com.example.desserts.model.QuestionResponseModel
import com.example.desserts.model.LoginModel
import com.example.desserts.model.QuestionModel
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
    }
}