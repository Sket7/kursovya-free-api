package com.sket.kursovyafreeapi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sket.kursovyafreeapi.data.local.converter.IntListConverter
import com.sket.kursovyafreeapi.data.local.dao.CachedQueryDao
import com.sket.kursovyafreeapi.data.local.dao.CharacterDao
import com.sket.kursovyafreeapi.data.local.dao.EpisodeDao
import com.sket.kursovyafreeapi.data.local.dao.EpisodeUserDataDao
import com.sket.kursovyafreeapi.data.local.dao.LocationDao
import com.sket.kursovyafreeapi.data.local.entity.CachedQueryEntity
import com.sket.kursovyafreeapi.data.local.entity.CachedQueryItemEntity
import com.sket.kursovyafreeapi.data.local.entity.CharacterEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import com.sket.kursovyafreeapi.data.local.entity.LocationEntity

@Database(
    entities = [
        CharacterEntity::class,
        EpisodeEntity::class,
        LocationEntity::class,
        EpisodeUserDataEntity::class,
        CachedQueryEntity::class,
        CachedQueryItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(IntListConverter::class)
abstract class RickAndMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun locationDao(): LocationDao
    abstract fun episodeUserDataDao(): EpisodeUserDataDao
    abstract fun cachedQueryDao(): CachedQueryDao

    companion object {
        const val DATABASE_NAME = "rick_and_morty.db"

        fun create(context: Context): RickAndMortyDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                RickAndMortyDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
