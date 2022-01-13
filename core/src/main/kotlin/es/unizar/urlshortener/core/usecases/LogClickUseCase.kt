package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.OffsetDateTime
import java.util.concurrent.Future

/**
 * Log that somebody has requested the redirection identified by a key.
 *
 * **Note**: This is an example of functionality.
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

    private fun checkData(key: String) {
        println("SLEEPING")
        Thread.sleep(1000)
        var shortUrl: ShortUrl? = shortUrlRepository.findByKey(key) ?: throw RedirectionNotFound(key)
        if (shortUrl != null) {
            val now = OffsetDateTime.now()
            val hourBefore = now.plusSeconds(-10)
            println("HOURS-" + hourBefore + "||" + shortUrl.lastInfo)
            if (shortUrl.lastInfo?.isBefore(hourBefore) == true) {
                getUsersCountUseCase.updateInfo(key)
                shortUrl = shortUrlRepository.findByKey(key) ?: throw RedirectionNotFound(key)
                shortUrl.lastInfo = now
                shortUrlRepository.save(shortUrl)
            }
        }
        println("FINISH")
    }
}
