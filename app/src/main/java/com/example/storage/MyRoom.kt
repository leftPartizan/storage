package com.example.storage

import androidx.lifecycle.LiveData
import androidx.room.*

class MyRoom {

    @Dao
    abstract class DaoStorage {

        @Query("SELECT editText FROM Storage WHERE ID=0")
        abstract fun getData(): LiveData<String>

        @Query("SELECT * FROM storage")
        abstract fun getAll(): List<Storage>

        @Query("INSERT OR REPLACE INTO Storage(id,editText) VALUES (0, :editText)")
        abstract fun saveData(editText: String)

        @Query("DELETE FROM Storage")
        abstract fun tableDestroyer()
    }

    @Entity
    data class Storage(
            @PrimaryKey
            val id: Int = 0,
            val editText: String)

    @Database(entities = [Storage::class], version = 1)
    abstract class StorageDataBase : RoomDatabase() {
        abstract fun getDataStorage(): DaoStorage
    }
}