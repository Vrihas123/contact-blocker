package com.vrihas.assignment.pratilipi.pratilipiapp.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User
import com.vrihas.assignment.pratilipi.pratilipiapp.room.dao.ContactDataDao
import com.vrihas.assignment.pratilipi.pratilipiapp.utils.DbConstants
import javax.inject.Singleton

@Database(entities = [User::class], version = 1, exportSchema = false)
@Singleton
public abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDataDao(): ContactDataDao

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
               val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, DbConstants.CONTACT_DATABASE_NAME
                ).allowMainThreadQueries()
                   .build()
                INSTANCE = instance
                return instance
            }

        }
    }


}