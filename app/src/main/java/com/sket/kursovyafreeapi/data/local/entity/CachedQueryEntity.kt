package com.sket.kursovyafreeapi.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "cached_queries")
data class CachedQueryEntity(
    val resourceType: String,
    val nextPage: Int?,
    val previousPage: Int?,
    val totalCount: Int,
    val totalPages: Int,
    val cachedAt: Long,
    @androidx.room.PrimaryKey
    val queryKey: String
)

@Entity(
    tableName = "cached_query_items",
    primaryKeys = ["queryKey", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = CachedQueryEntity::class,
            parentColumns = ["queryKey"],
            childColumns = ["queryKey"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["queryKey"]),
        Index(value = ["queryKey", "position"], unique = true)
    ]
)
data class CachedQueryItemEntity(
    val queryKey: String,
    val itemId: Int,
    val position: Int
)
