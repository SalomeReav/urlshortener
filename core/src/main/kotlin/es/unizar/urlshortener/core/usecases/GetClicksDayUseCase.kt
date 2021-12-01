package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.ShortUrlRepositoryService

/**
 *  Returns the clicks number for the current day from a url identified by it [id]
 */
interface GetClicksDayUseCase {
    fun getClicksDay(key: String) : ShortUrlProperties
}

/**
 * Implementation of [GetClicksDayUseCase].
 */
class GetClicksDayUseCaseImpl (
    private val ShortUrlRepository: ShortUrlRepositoryService
) : GetClicksDayUseCase {
    override fun getClicksDay(key: String) = ShortUrlRepository
        .findByKey(key)
        ?.properties
        ?: throw RedirectionNotFound(key)
}