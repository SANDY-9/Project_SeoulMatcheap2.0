package com.sandy.matcheap.data.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sandy.matcheap.domain.model.menu.Menu

interface MenuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMenu(menus: List<Menu>)

    @Query("DELETE FROM store_menu WHERE id = :id")
    suspend fun deleteMenu(id: String)

}