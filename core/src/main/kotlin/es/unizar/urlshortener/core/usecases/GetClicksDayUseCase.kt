package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.format.DateTimeFormatter

/**
 *  Returns the clicks number for the current day from a url identified by it [id]
 */
interface GetClicksDayUseCase {
    fun getClicksDay(hash: String, remoteUsr: String?): Map<String, Int>
}

/**
 * Implementation of [GetClicksDayUseCase].
 */
class GetClicksDayUseCaseImpl(
    private val clickRepository: ClickRepositoryService
) : GetClicksDayUseCase {
    override fun getClicksDay(hash: String, remoteUsr: String?): Map<String, Int> {
        var lastRemoteUser = clickRepository.findByHash(hash).last() //Get the lastRemoteUser who did a click
        if (remoteUsr == lastRemoteUser.properties.lastRemoteUser) return mutableMapOf<String, Int>(); //Avoid that user generate statistics on my url on each consecutive click.
        var list = clickRepository
            .findByHash(hash)
        if (list.isEmpty()) return mutableMapOf<String, Int>();
        val clickByDateList = list.groupBy { it.created.format(DateTimeFormatter.ISO_LOCAL_DATE) }
        return clickByDateList.map { it.key to it.value.size }.toMap();
    }
}