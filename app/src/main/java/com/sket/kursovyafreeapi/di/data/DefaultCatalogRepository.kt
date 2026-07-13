package com.sket.kursovyafreeapi.di.data

import androidx.room.withTransaction
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.local.entity.CachedQueryEntity
import com.sket.kursovyafreeapi.data.local.mapper.toEntity
import com.sket.kursovyafreeapi.data.local.mapper.toModel
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.toResourceIdsPath
import com.sket.kursovyafreeapi.data.remote.mapper.toModel
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.model.Page

class DefaultCatalogRepository(
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase,
    private val now: () -> Long = System::currentTimeMillis
) : CatalogRepository {
    override suspend fun cachedCharacters(query: CharacterQuery): CachedPage<Character>? {
        val metadata = database.cachedQueryDao().get(query.cacheKey()) ?: return null
        val ids = database.cachedQueryDao().getItemIds(query.cacheKey())
        val itemsById = database.characterDao().getByIds(ids).associateBy { it.id }
        return metadata.toPage(ids.mapNotNull(itemsById::get).map { it.toModel() })
    }

    override suspend fun refreshCharacters(query: CharacterQuery): CachedPage<Character> {
        val page = api.getCharacters(
            page = query.page,
            name = query.name.asApiFilter(),
            status = query.status.asApiFilter(),
            species = query.species.asApiFilter(),
            type = query.type.asApiFilter(),
            gender = query.gender.asApiFilter()
        ).toModel { it.toModel() }
        return cacheCharacters(query.cacheKey(), page)
    }

    override suspend fun cachedEpisodes(query: EpisodeQuery): CachedPage<Episode>? {
        val metadata = database.cachedQueryDao().get(query.cacheKey()) ?: return null
        val ids = database.cachedQueryDao().getItemIds(query.cacheKey())
        val itemsById = database.episodeDao().getByIds(ids).associateBy { it.id }
        return metadata.toPage(ids.mapNotNull(itemsById::get).map { it.toModel() })
    }

    override suspend fun refreshEpisodes(query: EpisodeQuery): CachedPage<Episode> {
        val page = api.getEpisodes(
            page = query.page,
            name = query.name.asApiFilter(),
            episode = query.code.asApiFilter()
        ).toModel { it.toModel() }
        return cacheEpisodes(query.cacheKey(), page)
    }

    override suspend fun cachedLocations(query: LocationQuery): CachedPage<Location>? {
        val metadata = database.cachedQueryDao().get(query.cacheKey()) ?: return null
        val ids = database.cachedQueryDao().getItemIds(query.cacheKey())
        val itemsById = database.locationDao().getByIds(ids).associateBy { it.id }
        return metadata.toPage(ids.mapNotNull(itemsById::get).map { it.toModel() })
    }

    override suspend fun refreshLocations(query: LocationQuery): CachedPage<Location> {
        val page = api.getLocations(
            page = query.page,
            name = query.name.asApiFilter(),
            type = query.type.asApiFilter(),
            dimension = query.dimension.asApiFilter()
        ).toModel { it.toModel() }
        return cacheLocations(query.cacheKey(), page)
    }

    override suspend fun cachedCharacter(id: Int): Character? =
        database.characterDao().getById(id)?.toModel()

    override suspend fun refreshCharacter(id: Int): Character {
        val model = api.getCharacter(id).toModel()
        database.characterDao().upsert(listOf(model.toEntity(now())))
        return model
    }

    override suspend fun cachedEpisode(id: Int): Episode? =
        database.episodeDao().getById(id)?.toModel()

    override suspend fun refreshEpisode(id: Int): Episode {
        val model = api.getEpisode(id).toModel()
        database.episodeDao().upsert(listOf(model.toEntity(now())))
        return model
    }

    override suspend fun cachedLocation(id: Int): Location? =
        database.locationDao().getById(id)?.toModel()

    override suspend fun refreshLocation(id: Int): Location {
        val model = api.getLocation(id).toModel()
        database.locationDao().upsert(listOf(model.toEntity(now())))
        return model
    }

    override suspend fun resolveCharacters(ids: List<Int>): RelatedItems<Character> {
        val orderedIds = ids.distinct()
        if (orderedIds.isEmpty()) return RelatedItems(emptyList(), isComplete = true)
        val cached = database.characterDao().getByIds(orderedIds).associateBy { it.id }.toMutableMap()
        val missing = orderedIds.filterNot(cached::containsKey)
        if (missing.isNotEmpty()) {
            runCatching {
                val loaded = if (missing.size == 1) {
                    listOf(api.getCharacter(missing.single()).toModel())
                } else {
                    api.getCharacters(missing.toResourceIdsPath()).map { it.toModel() }
                }
                database.characterDao().upsert(loaded.map { it.toEntity(now()) })
                loaded.forEach { cached[it.id] = it.toEntity(now()) }
            }
        }
        val items = orderedIds.mapNotNull(cached::get).map { it.toModel() }
        return RelatedItems(items, isComplete = items.size == orderedIds.size)
    }

    override suspend fun resolveEpisodes(ids: List<Int>): RelatedItems<Episode> {
        val orderedIds = ids.distinct()
        if (orderedIds.isEmpty()) return RelatedItems(emptyList(), isComplete = true)
        val cached = database.episodeDao().getByIds(orderedIds).associateBy { it.id }.toMutableMap()
        val missing = orderedIds.filterNot(cached::containsKey)
        if (missing.isNotEmpty()) {
            runCatching {
                val loaded = if (missing.size == 1) {
                    listOf(api.getEpisode(missing.single()).toModel())
                } else {
                    api.getEpisodes(missing.toResourceIdsPath()).map { it.toModel() }
                }
                database.episodeDao().upsert(loaded.map { it.toEntity(now()) })
                loaded.forEach { cached[it.id] = it.toEntity(now()) }
            }
        }
        val items = orderedIds.mapNotNull(cached::get).map { it.toModel() }
        return RelatedItems(items, isComplete = items.size == orderedIds.size)
    }

    private suspend fun cacheCharacters(key: String, page: Page<Character>): CachedPage<Character> {
        val cachedAt = now()
        database.withTransaction {
            database.characterDao().upsert(page.items.map { it.toEntity(cachedAt) })
            database.cachedQueryDao().replace(page.metadata(key, "character", cachedAt), page.items.map { it.id })
        }
        return page.toCachedPage(cachedAt)
    }

    private suspend fun cacheEpisodes(key: String, page: Page<Episode>): CachedPage<Episode> {
        val cachedAt = now()
        database.withTransaction {
            database.episodeDao().upsert(page.items.map { it.toEntity(cachedAt) })
            database.cachedQueryDao().replace(page.metadata(key, "episode", cachedAt), page.items.map { it.id })
        }
        return page.toCachedPage(cachedAt)
    }

    private suspend fun cacheLocations(key: String, page: Page<Location>): CachedPage<Location> {
        val cachedAt = now()
        database.withTransaction {
            database.locationDao().upsert(page.items.map { it.toEntity(cachedAt) })
            database.cachedQueryDao().replace(page.metadata(key, "location", cachedAt), page.items.map { it.id })
        }
        return page.toCachedPage(cachedAt)
    }
}

private fun CachedQueryEntity.toPage(items: List<Character>) = CachedPage(
    items = items,
    totalCount = totalCount,
    totalPages = totalPages,
    nextPage = nextPage,
    previousPage = previousPage,
    cachedAt = cachedAt
)

@JvmName("toEpisodePage")
private fun CachedQueryEntity.toPage(items: List<Episode>) = CachedPage(
    items = items,
    totalCount = totalCount,
    totalPages = totalPages,
    nextPage = nextPage,
    previousPage = previousPage,
    cachedAt = cachedAt
)

@JvmName("toLocationPage")
private fun CachedQueryEntity.toPage(items: List<Location>) = CachedPage(
    items = items,
    totalCount = totalCount,
    totalPages = totalPages,
    nextPage = nextPage,
    previousPage = previousPage,
    cachedAt = cachedAt
)

private fun <T> Page<T>.metadata(key: String, resource: String, cachedAt: Long) = CachedQueryEntity(
    resourceType = resource,
    nextPage = info.next.pageNumber(),
    previousPage = info.previous.pageNumber(),
    totalCount = info.count,
    totalPages = info.pages,
    cachedAt = cachedAt,
    queryKey = key
)

private fun <T> Page<T>.toCachedPage(cachedAt: Long) = CachedPage(
    items = items,
    totalCount = info.count,
    totalPages = info.pages,
    nextPage = info.next.pageNumber(),
    previousPage = info.previous.pageNumber(),
    cachedAt = cachedAt
)

private fun String?.pageNumber(): Int? = this
    ?.substringAfter("page=", missingDelimiterValue = "")
    ?.substringBefore('&')
    ?.toIntOrNull()


