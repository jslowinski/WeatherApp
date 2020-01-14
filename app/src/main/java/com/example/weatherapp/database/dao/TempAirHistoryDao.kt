package com.example.weatherapp.database.dao

import androidx.room.*
import com.example.weatherapp.database.entity.TempAirHistoryEntity
import io.reactivex.Maybe


@Dao
abstract class TempAirHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<TempAirHistoryEntity>)

    @Query("DELETE FROM tempairhistory")
    abstract fun removeAll()

    @Query("SELECT * FROM tempairhistory")
    abstract fun getTempAirHistory(): Maybe<List<TempAirHistoryEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<TempAirHistoryEntity>){
        removeAll()
        insert(entities)
    }
}