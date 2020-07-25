package com.vrihas.assignment.pratilipi.pratilipiapp.injection.module

import android.app.Application
import com.vrihas.assignment.pratilipi.pratilipiapp.data.repository.ContactDataRepository
import com.vrihas.assignment.pratilipi.pratilipiapp.room.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val mApplication: Application) {

    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        return AppDatabase.getDatabase(mApplication)
    }

    @Provides
    @Singleton
    fun provideContactDataRepository(): ContactDataRepository {
        return ContactDataRepository()
    }


}