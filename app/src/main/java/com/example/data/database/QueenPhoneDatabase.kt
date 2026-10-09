package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ContactPreferenceEntity::class, CallRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QueenPhoneDatabase : RoomDatabase() {
    abstract fun contactPreferenceDao(): ContactPreferenceDao
    abstract fun callRecordDao(): CallRecordDao

    companion object {
        @Volatile
        private var INSTANCE: QueenPhoneDatabase? = null

        fun getDatabase(context: Context): QueenPhoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QueenPhoneDatabase::class.java,
                    "queen_phone.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
