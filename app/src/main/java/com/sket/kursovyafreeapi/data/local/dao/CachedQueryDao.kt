package com.sket.kursovyafreeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.sket.kursovyafreeapi.data.local.entity.CachedQueryEntity
import com.sket.kursovyafreeapi.data.local.entity.CachedQueryItemEntity

@Dao
interface CachedQueryDao {
    @Query("SELECT * FROM cached_queries WHERE queryKey = :queryKey")
    suspend fun get(queryKey: String): CachedQueryEntity?

    @Query(
        "SELECT itemId FROM cached_query_items WHERE queryKey = :queryKey ORDER BY position"
    )
    suspend fun getItemIds(queryKey: String): List<Int>

    @Upsert
    suspend fun upsert(query: CachedQueryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CachedQueryItemEntity>)

    @Query("DELETE FROM cached_query_items WHERE queryKey = :queryKey")
    suspend fun deleteItems(queryKey: String)

    @Query("DELETE FROM cached_queries WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Transaction
    suspend fun replace(query: CachedQueryEntity, itemIds: List<Int>) {
        upsert(query)
        deleteItems(query.queryKey)
        insertItems(
            itemIds.mapIndexed { index, itemId ->
                CachedQueryItemEntity(
                    queryKey = query.queryKey,
                    itemId = itemId,
                    position = index
                )
            }
        )
    }
}
