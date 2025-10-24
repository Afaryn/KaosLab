package com.afaryn.kaoslab.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.afaryn.kaoslab.data.local.room.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun get(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(notification: NotificationEntity)

    @Query("DELETE FROM notification WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM notification")
    suspend fun deleteAll()
}