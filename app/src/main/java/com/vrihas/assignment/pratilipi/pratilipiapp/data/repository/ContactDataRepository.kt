package com.vrihas.assignment.pratilipi.pratilipiapp.data.repository

import com.vrihas.assignment.pratilipi.pratilipiapp.MyApplication
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User
import com.vrihas.assignment.pratilipi.pratilipiapp.room.db.AppDatabase
import javax.inject.Inject

class ContactDataRepository{

    @Inject
    lateinit var database: AppDatabase


    init {
        MyApplication.instance.getComponent()!!.inject(this)
    }

    fun getContacts() = database.contactDataDao().getAllContacts()

    fun getBlockedContacts(isBlocked: Boolean) = database.contactDataDao().getBlockedContacts(isBlocked)

    suspend fun insertContact(user: User) = database.contactDataDao().addContact(user)

    suspend fun updateContact(id : Int, isBlocked : Boolean) = database.contactDataDao().updateContact(id, isBlocked)

}