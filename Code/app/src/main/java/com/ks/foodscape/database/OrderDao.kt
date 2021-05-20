package com.ks.foodscape.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao{

    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query("SELECT * FROM orders_table")
    fun getAllOrders(): List<OrderEntity>

    @Query("DELETE FROM orders_table WHERE resId = :resId")
    fun deleteOrders(resId: String)
}