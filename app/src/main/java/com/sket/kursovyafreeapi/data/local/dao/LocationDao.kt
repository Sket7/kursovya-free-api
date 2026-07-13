package com.sket.kursovyafreeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sket.kursovyafreeapi.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations WHERE id = :id")
    fun observeById(id: Int): Flow<LocationEntity?>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Int): LocationEntity?

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<LocationEntity>

    @Query(
        """
        SELECT locations.* FROM locations
        INNER JOIN cached_query_items ON locations.id = cached_query_items.itemId
        WHERE cached_query_items.queryKey = :queryKey
        ORDER BY cached_query_items.position
        """
    )
    fun observeQuery(queryKey: String): Flow<List<LocationEntity>>

    @Upsert
    suspend fun upsert(items: List<LocationEntity>)
}
