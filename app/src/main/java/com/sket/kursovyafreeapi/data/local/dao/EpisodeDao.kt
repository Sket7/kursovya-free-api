package com.sket.kursovyafreeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sket.kursovyafreeapi.data.local.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE id = :id")
    fun observeById(id: Int): Flow<EpisodeEntity?>

    @Query("SELECT * FROM episodes WHERE id = :id")
    suspend fun getById(id: Int): EpisodeEntity?

    @Query("SELECT * FROM episodes WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<EpisodeEntity>

    @Query(
        """
        SELECT episodes.* FROM episodes
        INNER JOIN cached_query_items ON episodes.id = cached_query_items.itemId
        WHERE cached_query_items.queryKey = :queryKey
        ORDER BY cached_query_items.position
        """
    )
    fun observeQuery(queryKey: String): Flow<List<EpisodeEntity>>

    @Upsert
    suspend fun upsert(items: List<EpisodeEntity>)
}
