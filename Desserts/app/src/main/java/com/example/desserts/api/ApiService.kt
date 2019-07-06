package com.example.desserts.api

import com.example.desserts.model.LoginModel
import io.reactivex.Observable
import retrofit2.http.POST

class ApiService {

    interface ApiImpl {
        @POST("/session")
        fun requestLogin(): Observable<LoginModel>
    }

    companion object {
        fun requestLogin(): Observable<LoginModel> {
            return ApiClient.create(ApiImpl::class.java).requestLogin()
        }
    }
}