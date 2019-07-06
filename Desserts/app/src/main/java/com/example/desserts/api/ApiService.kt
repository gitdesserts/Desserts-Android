package com.example.desserts.api

import com.example.desserts.model.DataModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class ApiService {

    interface ApiImpl {
        @GET("/feed/geo:{lat};{lng}/?")
        fun getData(@Path("lat")lat: Double, @Path("lng")lng: Double, @Query("token") token: String): Observable<DataModel>
    }

    companion object {
        fun getData(lat: Double, lng: Double, token: String): Observable<DataModel> {
            return ApiClient.create(ApiImpl::class.java).getData(lat, lng, token)
        }
    }
}