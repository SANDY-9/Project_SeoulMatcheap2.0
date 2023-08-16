package com.sandy.seoul_matcheap.data.store

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.sandy.seoul_matcheap.data.store.dao.*
import com.sandy.seoul_matcheap.data.store.entity.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@Database(
    entities = [StoreInfo::class, SearchHistory::class, Bookmark::class, Polygon::class, Menu::class],
    version = 2,
    autoMigrations = [
        AutoMigration (
            from = 1,
            to = 2,
            spec = StoreDatabase.MyAutoMigration::class
        )
    ],
    exportSchema = true
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun storeDao() : StoreDao
    abstract fun mapDao() : MapDao
    abstract fun searchDao() : SearchDao
    abstract fun bookmarkDao() : BookmarkDao

    @DeleteColumn(tableName ="store_info", columnName ="codeName")
    @DeleteColumn(tableName ="store_info", columnName ="distance")
    class MyAutoMigration : AutoMigrationSpec

}