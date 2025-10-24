package com.afaryn.kaoslab.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.afaryn.kaoslab.data.local.room.entity.NotificationEntity

@Database(
    entities = [NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NotificationDatabase: RoomDatabase() {
    abstract val notificationDao: NotificationDao

    companion object {
        @Volatile
        private var instance: NotificationDatabase? = null

        fun getInstance(context: Context): NotificationDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): NotificationDatabase {
            return Room.databaseBuilder(
                context.applicationContext, NotificationDatabase::class.java, "kaoslab_db"
            ).fallbackToDestructiveMigration(true).build()
        }
    }
}