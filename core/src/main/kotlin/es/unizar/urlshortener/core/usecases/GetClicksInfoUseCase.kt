package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.time.format.DateTimeFormatter


/**
 *  getInfo: Returns statistics of a shortUrl identified by it [hash]
 *  updateInfo: Update statistics of a shortUrl identified by it [hash]
 */
interface GetClicksInfoUseCase {
    fun getInfo(hash: String): ClicksInfo
    fun updateInfo(hash: String)
}

/**
 * Implementation of [GetClicksInfoUseCase].
 */
class GetClicksInfoUseCaseImpl(
    private val clickRepository: ClickRepositoryService,
    private val shortUrlRepository: ShortUrlRepositoryService
) : GetClicksInfoUseCase {
    override fun getInfo(hash: String): ClicksInfo {
        val shortUrl: ShortUrl? = shortUrlRepository.findByKey(hash) ?: throw RedirectionNotFound(hash)
        return shortUrl!!.clicksInfo
    }

    override fun updateInfo(hash: String) {
        var shortUrl: ShortUrl? = shortUrlRepository.findByKey(hash) ?: throw RedirectionNotFound(hash)
        if (shortUrl != null) {
            var list = clickRepository.findByHash(hash)
            if (list.isNotEmpty()) {
                shortUrl.clicksInfo.usersCount = list.groupBy { it.properties.ip }.size
                shortUrl.clicksInfo.totalClicks = clickRepository.countByHash(hash)
                val clickByDateList = list.groupBy { it.created.format(DateTimeFormatter.ISO_LOCAL_DATE) }
                shortUrl.clicksInfo.numClicksDay = clickByDateList.map { it.key to it.value.size }.toMap();
                shortUrlRepository.save(shortUrl)
            }
        }
    }
}

