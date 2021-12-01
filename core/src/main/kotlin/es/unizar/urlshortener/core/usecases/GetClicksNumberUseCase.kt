package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.ShortUrlRepositoryService

/**
 *  Returns the clicks number from a url identified by it [id]
 */
interface GetClicksNumberUseCase {
    fun getClicksNumber(key: String) : ShortUrlProperties
}

/**
 * Implementation of [GetClicksNumberUseCase].
 */
class GetClicksNumberUseCaseImpl (
    private val ShortUrlRepository: ShortUrlRepositoryService
    ) : GetClicksNumberUseCase {
        override fun getClicksNumber(key: String) = ShortUrlRepository
            .findByKey(key)
            ?.properties
            ?: throw RedirectionNotFound(key)
    }