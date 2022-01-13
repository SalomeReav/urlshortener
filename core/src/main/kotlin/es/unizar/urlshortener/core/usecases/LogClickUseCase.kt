package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.OffsetDateTime

/**
 * Log that somebody has requested the redirection identified by a key.
 */
interface LogClickUseCase {
    fun logClick(key: String, data: ClickProperties)
}

/**
 * Implementation of [LogClickUseCase].
 */
class LogClickUseCaseImpl(
    private val clickRepository: ClickRepositoryService,
    private val getUsersCountUseCase: GetClicksInfoUseCase,
    private val shortUrlRepository: ShortUrlRepositoryService
) : LogClickUseCase {
    override fun logClick(key: String, data: ClickProperties) {
        val cl = Click(
            hash = key,
            properties = ClickProperties(
                ip = data.ip
            ),
            created = OffsetDateTime.now()
        )
        checkData(key)
        clickRepository.save(cl)
    }

    // Check if it is time to update the click statistics
    private fun checkData(key: String) {
        var shortUrl: ShortUrl? = shortUrlRepository.findByKey(key) ?: throw RedirectionNotFound(key)
        if (shortUrl != null) {
            val now = OffsetDateTime.now()
            val tenSecBefore = now.plusSeconds(-10)
            if (shortUrl.lastInfo?.isBefore(tenSecBefore) == true) {
                getUsersCountUseCase.updateInfo(key)
                shortUrl = shortUrlRepository.findByKey(key) ?: throw RedirectionNotFound(key)
                shortUrl.lastInfo = now
                shortUrlRepository.save(shortUrl)
            }
        }
    }
}
