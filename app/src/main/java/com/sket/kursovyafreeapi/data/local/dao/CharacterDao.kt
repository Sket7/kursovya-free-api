package com.sket.kursovyafreeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sket.kursovyafreeapi.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE id = :id")
    fun observeById(id: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Int): CharacterEntity?

    @Query("SELECT * FROM characters WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<CharacterEntity>

    @Query(
        """
        SELECT characters.* FROM characters
        INNER JOIN cached_query_items ON characters.id = cached_query_items.itemId
        WHERE cached_query_items.queryKey = :queryKey
        ORDER BY cached_query_items.position
        """
    )
    fun observeQuery(queryKey: String): Flow<List<CharacterEntity>>

    @Upsert
    suspend fun upsert(items: List<CharacterEntity>)
}
