package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.ShortUrlRepositoryService

/**
 *  Returns the users count who pressed on a url identified by it [id]
 */
interface GetUsersCountUseCase {
    fun getUsersCount(key: String) : ShortUrlProperties
}

/**
 * Implementation of [GetUsersCountUseCase].
 */
class GetUsersCountUseCaseImpl (
    private val ShortUrlRepository: ShortUrlRepositoryService
    ) : GetUsersCountUseCase {
        override fun getUsersCount(key: String) = ShortUrlRepository
            .findByKey(key)
            ?.properties
            ?: throw RedirectionNotFound(key)
}

