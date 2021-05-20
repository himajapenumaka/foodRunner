package com.ks.foodscape.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRes(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRes(restaurantEntity: RestaurantEntity)

    @Query("select * from Restaurant_table")
    fun getAllRes(): List<RestaurantEntity>

    @Query("select * from Restaurant_table where resId= :res_Id")
    fun getResById(res_Id: String): RestaurantEntity

}