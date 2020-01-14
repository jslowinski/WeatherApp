package com.example.weatherapp.database.dao

import androidx.room.*
import com.example.weatherapp.database.entity.CurrentEntity
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class CurrentDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: CurrentEntity)

    @Query("DELETE FROM current")
    abstract fun removeCurrent()

    @Query("SELECT * FROM current")
    abstract fun getCurrent() : Maybe<CurrentEntity>

    @Transaction
    open fun removeAndInsert(entities: CurrentEntity){
        removeCurrent()
        insert(entities)
    }
}