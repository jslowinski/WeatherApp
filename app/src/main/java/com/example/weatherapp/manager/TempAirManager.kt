package com.example.weatherapp.manager

import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.TempAirHistoryDto
import com.example.weatherapp.database.AndroidDatabase
import com.example.weatherapp.database.entity.TempAirHistoryEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TempAirManager {

    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    fun dowloadTempAirHistory(url: String): Completable =
        apiService
            .fetchHistory(url)
            .flatMapCompletable {
                saveTempAirHistory(it)
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveTempAirHistory(tempAirHistoryDto: List<TempAirHistoryDto>) =
        Completable.fromAction{
            val entities = tempAirHistoryDto.map {
                TempAirHistoryEntity(
                    it.time,
                    it.tempair
                )
            }
            database.tempAirHistoryDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    fun getTempAirHistory(): Maybe<List<TempAirHistoryEntity>> =
        database
            .tempAirHistoryDao()
            .getTempAirHistory()
            .subscribeOn(Schedulers.io())
}