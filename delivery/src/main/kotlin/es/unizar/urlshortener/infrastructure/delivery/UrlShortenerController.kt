package es.unizar.urlshortener.infrastructure.delivery

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import es.unizar.urlshortener.core.ClickProperties
import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import es.unizar.urlshortener.core.usecases.CreateQrCodeUseCase
import es.unizar.urlshortener.core.usecases.GetQrImageUseCase
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.servlet.http.HttpServletRequest
import boofcv.io.image.ConvertBufferedImage
import java.awt.image.BufferedImage
import boofcv.kotlin.asBufferedImage
import boofcv.gui.image.ShowImages
import boofcv.alg.fiducial.qrcode.QrCodeEncoder
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage
import org.springframework.core.io.ClassPathResource
import org.springframework.util.StreamUtils
import boofcv.struct.image.GrayU8;
import boofcv.io.image.ConvertRaster
import boofcv.io.image.UtilImageIO
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


/**
 * The specification of the controller.
 */
interface UrlShortenerController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void>

    /**
     * Creates a short url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut>
    fun getQrImage(id: String, request: HttpServletRequest): ResponseEntity<ByteArray>
}

/**
 * Data required to create a short url.
 */
data class ShortUrlDataIn(
    val url: String,
    val createQR: Boolean? = false,
    val sponsor: String? = null
)

/**
 * Data returned after the creation of a short url.
 */
data class ShortUrlDataOut(
    val url: URI? = null,
    var qr: URI? = null,
    val properties: Map<String, Any> = emptyMap()
)


/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class UrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase,
    val createQrCodeUseCase: CreateQrCodeUseCase,
    val getQrImageUseCase: GetQrImageUseCase
) : UrlShortenerController {

    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.target)
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.mode))
        }
    @GetMapping("/qr/{id:.*}")
    override fun getQrImage(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ByteArray> =
            getQrImageUseCase.getQrImage(id).let {
                val h = HttpHeaders()
                h.setContentType(MediaType.IMAGE_PNG)
                val baos = ByteArrayOutputStream();
                ImageIO.write(it.gray?.asBufferedImage(), "png", baos);
                ResponseEntity<ByteArray>(baos.toByteArray(), h, HttpStatus.OK)
            }
            
        
    @PostMapping("/api/link", consumes = [ MediaType.APPLICATION_FORM_URLENCODED_VALUE ])
    override fun shortener(data: ShortUrlDataIn, request: HttpServletRequest): ResponseEntity<ShortUrlDataOut> =
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor
            )
        ).let {
            val h = HttpHeaders()
            val url = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            h.location = url
            var response = ShortUrlDataOut(
                url = url,
                properties = mapOf(
                    "safe" to it.properties.safe
                )
            )
            if(data.createQR==true){
                createQrCodeUseCase.create(url).let{
                   response.qr = linkTo<UrlShortenerControllerImpl> { getQrImage(it.hash, request) }.toUri()     
				};
			}
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }
}
