package com.aarogyaforworkers.aarogya.storage.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aarogyaforworkers.aarogya.storage.Local.Models.LocalSession

@Database(entities = [LocalSession::class], version = 2)
abstract class LocalSessionsDB : RoomDatabase(){
    abstract fun sessionDao(): SessionDao
}