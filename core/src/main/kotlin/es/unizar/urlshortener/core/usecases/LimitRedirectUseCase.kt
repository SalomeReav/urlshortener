package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 *  limitRedirectByDay:
 *      Return if it is possible to do the redirection to a url identified by it [id]
 *  updateLastRedirect:
 *      Update the time of the last redirection to a url identified by it [id]
 */
interface LimitRedirectUseCase {
    fun limitRedirectByDay(hash: String): Boolean
    fun updateLastRedirect(hash: String, now: OffsetDateTime)
}

/**
 * Implementation of [LimitRedirectUseCase].
 */
class LimitRedirectUseCaseImpl(
    private val shortUrlRepositoryService: ShortUrlRepositoryService
) : LimitRedirectUseCase {
    private var _limitByDay = 6;
    override fun limitRedirectByDay(hash: String): Boolean {
        var available = false
        val shortUrl = shortUrlRepositoryService.findByKey(hash)
        if (shortUrl != null && shortUrl.redirectCount!! < _limitByDay)
            available = true
        return available
    }

    override fun updateLastRedirect(hash: String, now: OffsetDateTime) {
        val shortUrl = shortUrlRepositoryService.findByKey(hash)
        val dateFormat = DateTimeFormatter.ISO_LOCAL_DATE
        if (shortUrl != null) {
            if (shortUrl.lastRedirect?.format(dateFormat).equals(now.format(dateFormat)))
                shortUrl.redirectCount = shortUrl.redirectCount?.plus(1)
            else
                shortUrl.redirectCount = 0
            shortUrl.lastRedirect = now
            shortUrlRepositoryService.save(shortUrl)
        }
    }
}