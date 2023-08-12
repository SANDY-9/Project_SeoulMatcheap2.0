package com.sandy.seoul_matcheap.data.store

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun storeDao() : StoreDao
    abstract fun mapDao() : MapDao
    abstract fun searchDao() : SearchDao
    abstract fun bookmarkDao() : BookmarkDao

}