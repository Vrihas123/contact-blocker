package com.vrihas.assignment.pratilipi.pratilipiapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User
import com.vrihas.assignment.pratilipi.pratilipiapp.utils.DbConstants

@Dao
interface ContactDataDao {

    @Query("SELECT * FROM " + DbConstants.CONTACT_TABLE + " ORDER BY " + DbConstants.CONTACT_NAME)
    fun getAllContacts() : LiveData<MutableList<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContact(user: User)

    @Query("UPDATE " + DbConstants.CONTACT_TABLE + " SET " + DbConstants.CONTACT_BLOCKED + " = :isBlocked" + " WHERE " + DbConstants.CONTACT_ID + " = :id ")
    suspend fun updateContact(id: Int, isBlocked: Boolean)

    @Query("SELECT * FROM " + DbConstants.CONTACT_TABLE + " WHERE " + DbConstants.CONTACT_BLOCKED + " = :isBlocked")
    fun getBlockedContacts(isBlocked: Boolean) : MutableList<User>
}