package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 *  Returns the clicks number for the current day from a url identified by it [id]
 */
interface LimitRedirectUseCase {
    fun limitRedirectByDay(hash: String) : Boolean
}

/**
 * Implementation of [GetClicksDayUseCase].
 */
class LimitRedirectUseCaseImpl (
    private val clickRepository: ClickRepositoryService
) : LimitRedirectUseCase {
    var LIMIT_BY_DAY = 10;
    override fun limitRedirectByDay(hash: String): Boolean {
        val clickByDateList = clickRepository.findByHash(hash)
            .groupBy { it.created.format(DateTimeFormatter.ISO_LOCAL_DATE) }
        val numClicksByDate = clickByDateList.map { it.key to it.value.size }.toMap()
        val todayClicks = numClicksByDate[OffsetDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)]
        var available = true
        if (todayClicks != null) {
            available = todayClicks < LIMIT_BY_DAY
        }
        return available;
    }
}