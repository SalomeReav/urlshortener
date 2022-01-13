package es.unizar.urlshortener.core.usecases

import boofcv.alg.fiducial.qrcode.QrCodeEncoder
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage
import boofcv.kotlin.asBufferedImage
import es.unizar.urlshortener.core.QrCode
import es.unizar.urlshortener.core.QrCodeRepositoryService
import java.io.ByteArrayOutputStream
import java.net.URI
import javax.imageio.ImageIO

interface CreateQrCodeUseCase {
    //Given an url returns the key that is used to create a Qr Code url.
    fun create(url: URI): QrCode
    //Given an url returns the qrCode image that represents the url
    fun createQRImage(url: URI): QrCode
}

/**
 * Implementation of [CreateQrCodeUseCase].
 */
class CreateQrCodeUseCaseImpl(
    private val qrCodeRepository: QrCodeRepositoryService,
) : CreateQrCodeUseCase {
    override fun create(url: URI): QrCode {
        val qu = QrCode(
            hash = getHashFromUrl(url),
            url = url.toString()
        )
        return qrCodeRepository.save(qu);
    }

    override fun createQRImage(url: URI): QrCode {
        val key: String = getHashFromUrl(url)
        var qrCode = qrCodeRepository.findByKey(key)

        if (qrCode == null) {//If not exist, create a new QrCode instance
            qrCode = QrCode(
                hash = key,
                image = getByteArray(url),
                url = url.toString()
            )
        } else if (qrCode.image == null) { //QrCodeImage has not been created yet
            qrCode.image = getByteArray(url)
        }
        return qrCodeRepository.save(qrCode)
    }

    private fun getByteArray(url: URI): ByteArray {
        // Create the QrCode data structure with the url.
        val qr = QrCodeEncoder().addAutomatic(url.toString()).fixate()
        // Render the QR Code into a BoofCV style image
        // 15 = pixelsPerModule (square)
        val generator = QrCodeGeneratorImage(15).render(qr)
        val output = ByteArrayOutputStream()
        ImageIO.write(generator.gray?.asBufferedImage(), "png", output)
        return output.toByteArray()
    }

    private fun getHashFromUrl(url: URI): String {
        val urlString = url.toString()
        return urlString.substring(urlString.lastIndexOf("-") + 1)
    }
}
