package com.vrihas.assignment.pratilipi.pratilipiapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vrihas.assignment.pratilipi.pratilipiapp.utils.DbConstants

@Entity(tableName = DbConstants.CONTACT_TABLE)
data class User (
    @PrimaryKey
    @ColumnInfo(name = DbConstants.CONTACT_ID)
    val id : Int,
    @ColumnInfo(name = DbConstants.CONTACT_NAME)
    val name: String?,
    @ColumnInfo(name = DbConstants.CONTACT_NUMBER)
    val number: String?,
    @ColumnInfo(name = DbConstants.CONTACT_BLOCKED)
    val blocked: Boolean

)