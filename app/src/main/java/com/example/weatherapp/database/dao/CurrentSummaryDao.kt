package com.example.weatherapp.database.dao

import androidx.room.*
import com.example.weatherapp.database.entity.CurrentSummaryEntity
import io.reactivex.Maybe

@Dao
abstract class CurrentSummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: CurrentSummaryEntity)

    @Query("DELETE FROM currentsummary")
    abstract fun removeCurrentSummary()

    @Query("SELECT * FROM currentsummary")
    abstract fun getCurrentSummary() : Maybe<CurrentSummaryEntity>

    @Transaction
    open fun removeAndInsert(entities: CurrentSummaryEntity){
        removeCurrentSummary()
        insert(entities)
    }
}