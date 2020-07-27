package com.vrihas.assignment.pratilipi.pratilipiapp.injection.component

import com.vrihas.assignment.pratilipi.pratilipiapp.data.repository.ContactDataRepository
import com.vrihas.assignment.pratilipi.pratilipiapp.injection.module.ApplicationModule
import com.vrihas.assignment.pratilipi.pratilipiapp.room.db.AppDatabase
import com.vrihas.assignment.pratilipi.pratilipiapp.ui.activity.HomeActivity
import com.vrihas.assignment.pratilipi.pratilipiapp.viewmodel.HomeViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun getDatabase(): AppDatabase

    fun getContactDataRepository(): ContactDataRepository

    fun inject(homeActivity: HomeActivity)

    fun inject(homeViewModel: HomeViewModel)

    fun inject(contactDataRepository: ContactDataRepository)

}