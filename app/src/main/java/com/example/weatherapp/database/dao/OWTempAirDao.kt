package com.example.weatherapp.database.dao

import androidx.room.*
import com.example.weatherapp.database.entity.OWTempAirEntity
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class OWTempAirDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<OWTempAirEntity>)

    @Query("DELETE FROM owtempair")
    abstract fun removeAll()

    @Query("SELECT * FROM owtempair")
    abstract fun getOWTempair(): Maybe<List<OWTempAirEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<OWTempAirEntity>) {
        removeAll()
        insert(entities)
    }
}