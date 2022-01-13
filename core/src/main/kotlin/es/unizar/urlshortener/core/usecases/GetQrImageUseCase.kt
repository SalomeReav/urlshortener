package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.QrCode
import es.unizar.urlshortener.core.QrCodeNotCreated
import es.unizar.urlshortener.core.QrCodeNotFound
import es.unizar.urlshortener.core.QrCodeRepositoryService

/**
 * Given a key returns a [QrCode] that contains a [QrCode image][QrCode.image]
 */
interface GetQrImageUseCase {
    fun getQrImage(key: String): QrCode
}

/**
 * Implementation of [GetQrImageUseCase].
 */
class GetQrImageUseCaseImpl(
    private val qrCodeRepository: QrCodeRepositoryService,
) : GetQrImageUseCase {
    override fun getQrImage(key: String): QrCode {
        var qrCode = qrCodeRepository
            .findByKey(key) ?: throw QrCodeNotFound(key)
        if (qrCode.image == null)
            throw QrCodeNotCreated(qrCode.url)
        return qrCode
    }
}

