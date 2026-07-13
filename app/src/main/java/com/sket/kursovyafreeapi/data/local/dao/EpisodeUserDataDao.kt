package com.sket.kursovyafreeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeUserDataDao {
    @Query("SELECT * FROM episode_user_data WHERE episodeId = :episodeId")
    fun observeByEpisodeId(episodeId: Int): Flow<EpisodeUserDataEntity?>

    @Query("SELECT * FROM episode_user_data")
    fun observeAll(): Flow<List<EpisodeUserDataEntity>>

    @Upsert
    suspend fun upsert(item: EpisodeUserDataEntity)

    @Query("DELETE FROM episode_user_data WHERE episodeId = :episodeId")
    suspend fun delete(episodeId: Int)
}
