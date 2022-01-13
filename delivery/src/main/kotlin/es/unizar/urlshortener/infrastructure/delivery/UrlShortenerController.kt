package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import es.unizar.urlshortener.core.usecases.*
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.OffsetDateTime
import java.util.concurrent.*
import javax.servlet.http.HttpServletRequest
import kotlin.concurrent.thread


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

    /**
     * Returns a image from a url identified by it [id].
     *
     * **Note**: Delivery of use case [GetQrImageUseCase].
     */
    fun getQrImage(id: String, request: HttpServletRequest): ResponseEntity<ByteArray>

    /**
     * Returns the clicks number, clicks number filtered by date and the users count who
     * pressed on a url identified by it [id]
     *
     * **Note**: Delivery of use cases [GetClicksNumbersUseCase] [GetClicksDayUseCase] [GetClicksInfoUseCase].
     */
    fun getClicksInfo(id: String, request: HttpServletRequest): ResponseEntity<ClicksInfo>
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
    val getQrImageUseCase: GetQrImageUseCase,
    val getClicksInfoUseCase: GetClicksInfoUseCase,
    val limitRedirectUseCase: LimitRedirectUseCase,
    val qrQueue: BlockingQueue<String>,
    val limitQueue: BlockingQueue<TimeOfRedirection>,
) : UrlShortenerController {

    @Autowired
    @Async("taskExecutorQrCode")
    fun startQrConsumers() {
        for (i in 0..3)
            thread {
                QRConsumer(qrQueue, createQrCodeUseCase).run()
            }
    }

    @Autowired
    @Async("taskExecutorLimit")
    fun startLimitConsumers() {
        for (i in 0..3)
            thread {
                LimitConsumer(limitQueue, limitRedirectUseCase).run()
            }
    }


    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        limitRedirectUseCase.limitRedirectByDay(id).let {
            if (it) {
                limitQueue.put(TimeOfRedirection(id, OffsetDateTime.now()))
                redirectUseCase.redirectTo(id).let {
                    logClickUseCase.logClick(
                        id, ClickProperties(ip = request.remoteAddr)
                    )
                    val h = HttpHeaders()
                    h.location = URI.create(it.target)
                    return ResponseEntity<Void>(h, HttpStatus.valueOf(it.mode))
                }
            } else {
                throw UnavailableUrl(request.requestURL.toString())
            }
        }

    @GetMapping("/qr/{id:.*}")
    override fun getQrImage(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ByteArray> =
        getQrImageUseCase.getQrImage(id).let {
            val h = HttpHeaders()
            h.contentType = MediaType.IMAGE_PNG
            if (it.image == null) {
                val qr = createQrCodeUseCase.createQRImage(URI(it.url))
                ResponseEntity<ByteArray>(qr.image, h, HttpStatus.OK)
            }
            ResponseEntity<ByteArray>(it.image, h, HttpStatus.OK)
        }

    @GetMapping("/{id:.*}.json")
    override fun getClicksInfo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<ClicksInfo> =
        redirectUseCase.redirectTo(id).let {
            val data = getClicksInfoUseCase.getInfo(id)
            return ResponseEntity<ClicksInfo>(data, HttpStatus.OK)
        }

    @PostMapping("/api/link", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
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
            if (data.createQR == true) {
                createQrCodeUseCase.create(url).let { qc ->
                    response.qr = linkTo<UrlShortenerControllerImpl> { getQrImage(qc.hash, request) }.toUri()
                    qrQueue.put(qc.url)
                }
            }
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }
}
