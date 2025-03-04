package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ResponseBody
    @ExceptionHandler(value = [InvalidUrlException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun invalidUrls(ex: InvalidUrlException) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [RedirectionNotFound::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected fun redirectionNotFound(ex: RedirectionNotFound) = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [QrCodeNotFound::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected fun qrCodeNotFound(ex: QrCodeNotFound) = ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.message)


    @ResponseBody
    @ExceptionHandler(value = [UnavailableUrl::class])
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    protected fun unavailableUrl(ex: UnavailableUrl) = ErrorMessage(HttpStatus.TOO_MANY_REQUESTS.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [UrlNotReachable::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun urlNotReachable(ex: UrlNotReachable) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [UrlNotChecked::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun urlNotValidatedYet(ex: UrlNotChecked) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [QrCodeNotCreated::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun urlNotCreatedYet(ex: QrCodeNotCreated) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)

    @ResponseBody
    @ExceptionHandler(value = [UrlNotSafe::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected fun urlNotSafe(ex: UrlNotSafe) = ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.message)
}

data class ErrorMessage(
    val statusCode: Int,
    val message: String?,
    val timestamp: String = DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.now())
)
