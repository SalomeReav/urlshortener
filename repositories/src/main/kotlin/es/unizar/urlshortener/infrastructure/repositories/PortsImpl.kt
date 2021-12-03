package es.unizar.urlshortener.infrastructure.repositories

import es.unizar.urlshortener.core.Click
import es.unizar.urlshortener.core.ClickRepositoryService
import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.ShortUrlRepositoryService
import es.unizar.urlshortener.core.QrCode
import es.unizar.urlshortener.core.QrCodeRepositoryService
import org.springframework.data.jpa.repository.Modifying
import java.time.OffsetDateTime

/**
 * Implementation of the port [ClickRepositoryService].
 */
class ClickRepositoryServiceImpl(
    private val clickEntityRepository: ClickEntityRepository
) : ClickRepositoryService {
    override fun save(cl: Click): Click = clickEntityRepository.save(cl.toEntity()).toDomain()
    override fun findByHash(hash: String): List<Click> {
        return clickEntityRepository.findByHash(hash).map {  Click(hash = it.hash, created = it.created) }
    }
    override fun countByHash(hash:String): Int{
        return clickEntityRepository.countByHash(hash)
    }
}

/**
 * Implementation of the port [ShortUrlRepositoryService].
 */
class ShortUrlRepositoryServiceImpl(
    private val shortUrlEntityRepository: ShortUrlEntityRepository
) : ShortUrlRepositoryService {
    override fun findByKey(id: String): ShortUrl? = shortUrlEntityRepository.findByHash(id)?.toDomain()

    override fun save(su: ShortUrl): ShortUrl = shortUrlEntityRepository.save(su.toEntity()).toDomain()
}

/**
 * Implementation of the port [QrCodeRepositoryServiceImpl].
 */
class QrCodeRepositoryServiceImpl(
    private val qrCodeEntityRepository: QrCodeEntityRepository
) : QrCodeRepositoryService {
    override fun findByKey(id: String): QrCode? = qrCodeEntityRepository.findByHash(id)?.toDomain()

    override fun save(qc: QrCode): QrCode = qrCodeEntityRepository.save(qc.toEntity()).toDomain()
}
