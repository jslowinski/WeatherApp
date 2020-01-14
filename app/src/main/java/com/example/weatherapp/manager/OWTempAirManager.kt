package com.example.weatherapp.manager

import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.OWRDto
import com.example.weatherapp.api.model.OWRMainDto
import com.example.weatherapp.database.AndroidDatabase
import com.example.weatherapp.database.entity.OWTempAirEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class OWTempAirManager {

    //region API

    private val apiService by lazy {
        ApiClient.createOWA()
    }

    fun downloadOWTempAir(url: String): Completable =
        apiService
            .fetchOWAForecast(url)
            .flatMapCompletable {
                saveOWTempAir(it.list)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //regionDatabase

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveOWTempAir(owrDto: List<OWRDto<OWRMainDto>>) =
        Completable.fromAction{
            val entities = owrDto.map {
                OWTempAirEntity(
                    it.dt,
                    it.main.temp
                )
            }
            database.oWTempAirDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    fun getOWTempAir(): Maybe<List<OWTempAirEntity>> =
        database
            .oWTempAirDao()
            .getOWTempair()
            .subscribeOn(Schedulers.io())
}