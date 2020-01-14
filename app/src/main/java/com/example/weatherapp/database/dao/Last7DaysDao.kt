package com.example.weatherapp.database.dao

import androidx.room.*
import com.example.weatherapp.database.entity.Last7DaysEntity
import io.reactivex.Maybe

@Dao
abstract class Last7DaysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<Last7DaysEntity>)

    @Query("DELETE FROM last7days")
    abstract fun removeAll()

    @Query("SELECT * FROM last7days")
    abstract fun getLast7days(): Maybe<List<Last7DaysEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<Last7DaysEntity>){
        removeAll()
        insert(entities)
    }
}