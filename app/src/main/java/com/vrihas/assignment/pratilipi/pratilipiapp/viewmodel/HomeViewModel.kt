package com.vrihas.assignment.pratilipi.pratilipiapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrihas.assignment.pratilipi.pratilipiapp.MyApplication
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User
import com.vrihas.assignment.pratilipi.pratilipiapp.data.repository.ContactDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
    View Model class performing database actions via contact data repository
 */

class HomeViewModel : ViewModel() {

    @Inject
    lateinit var contactDataRepository: ContactDataRepository


    val allContacts: LiveData<MutableList<User>>

    init {
        MyApplication.instance.getComponent().inject(this)
        allContacts = contactDataRepository.getContacts()
    }

    fun insertContact(user: User) = viewModelScope.launch(Dispatchers.IO) {
        contactDataRepository.insertContact(user)
    }

    fun updateContact(id : Int, isBlocked : Boolean) = viewModelScope.launch {
        contactDataRepository.updateContact(id, isBlocked)
    }

}