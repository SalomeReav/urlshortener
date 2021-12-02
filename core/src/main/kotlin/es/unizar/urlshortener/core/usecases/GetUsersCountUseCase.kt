package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.ClickRepositoryService
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.ShortUrlRepositoryService
import java.time.format.DateTimeFormatter

/**
 *  Returns the users count who pressed on a url identified by it [id]
 */
interface GetUsersCountUseCase {
    fun getUsersCount(hash: String) : Int
}

/**
 * Implementation of [GetUsersCountUseCase].
 */
class GetUsersCountUseCaseImpl (
    private val clickRepository: ClickRepositoryService
    ) : GetUsersCountUseCase {
        override fun getUsersCount(hash: String): Int {
            var list = clickRepository.findByHash(hash)
            if (list.isEmpty()) throw RedirectionNotFound(hash)
            return list.groupBy { it.properties.ip }.size;
        }
}

