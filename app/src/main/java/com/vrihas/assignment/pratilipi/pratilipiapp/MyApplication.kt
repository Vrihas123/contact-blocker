package com.vrihas.assignment.pratilipi.pratilipiapp

import android.app.Application
import com.vrihas.assignment.pratilipi.pratilipiapp.injection.component.ApplicationComponent
import com.vrihas.assignment.pratilipi.pratilipiapp.injection.component.DaggerApplicationComponent
import com.vrihas.assignment.pratilipi.pratilipiapp.injection.module.ApplicationModule

class MyApplication : Application() {

    lateinit var mApplicationComponent: ApplicationComponent

    // Creating single instance of Application
    companion object {
        lateinit var instance : MyApplication
        private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getComponent(): ApplicationComponent {
            mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        return mApplicationComponent
    }
}