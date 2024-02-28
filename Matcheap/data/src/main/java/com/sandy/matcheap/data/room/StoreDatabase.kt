package com.sandy.matcheap.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.sandy.matcheap.data.remote.menu.model.MenuDTO
import com.sandy.matcheap.data.room.dao.BookmarkDao
import com.sandy.matcheap.data.room.dao.CountDao
import com.sandy.matcheap.data.room.dao.MapDao
import com.sandy.matcheap.data.room.dao.MenuDao
import com.sandy.matcheap.data.room.dao.SearchDao
import com.sandy.matcheap.data.room.dao.StoreDao
import com.sandy.matcheap.data.room.entity.Bookmark
import com.sandy.matcheap.domain.model.map.Polygon
import com.sandy.matcheap.data.room.entity.SearchHistory
import com.sandy.matcheap.domain.model.store.StoreDetails

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-17
 * @desc
 */

@Database(
    entities = [StoreDetails::class, SearchHistory::class, Bookmark::class, Polygon::class, MenuDTO::class],
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

    abstract fun storeDao(): StoreDao
    abstract fun countDao(): CountDao
    abstract fun mapDao(): MapDao
    abstract fun searchDao(): SearchDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun menuDao(): MenuDao

    @DeleteColumn(tableName ="store_info", columnName ="codeName")
    @DeleteColumn(tableName ="store_info", columnName ="distance")
    class MyAutoMigration : AutoMigrationSpec

}