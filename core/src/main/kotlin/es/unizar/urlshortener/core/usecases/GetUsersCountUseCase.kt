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
    fun getUsersCount(hash: String, remoteUsr: String) : Int
}

/**
 * Implementation of [GetUsersCountUseCase].
 */
class GetUsersCountUseCaseImpl (
    private val clickRepository: ClickRepositoryService
    ) : GetUsersCountUseCase {
        override fun getUsersCount(hash: String, remoteUsr: String): Int {
            var lastRemoteUser = clickRepository.findByHash(hash).last() //Get the lastRemoteUser who did a click
            if (remoteUsr == lastRemoteUser.properties.lastRemoteUser) return 0 //Avoid that user generate statistics on my url on each consecutive click.
            var list = clickRepository.findByHash(hash)
            if (list.isEmpty()) return 0
            return list.groupBy { it.properties.ip }.size;
        }
}

