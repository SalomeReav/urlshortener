package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture
import es.unizar.urlshortener.core.ValidatorService
import org.apache.commons.validator.routines.UrlValidator
import java.nio.charset.StandardCharsets
import es.unizar.urlshortener.core.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.net.URLEncoder
import java.net.HttpURLConnection

private const val CONNECTION_TIMEOUT = 3000L

/**
 * Implementation of the port [ValidatorService].
 */
open class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String) = urlValidator.isValid(url)
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = CONNECTION_TIMEOUT
        }
    }
    @Async("taskExecutorReachable")
    open override fun isReachable(url : String) : CompletableFuture<Boolean> {
        val response: HttpResponse?
        runBlocking {
            response = try { client.get(url) }
            catch (e: Exception) { null }
        }
        return CompletableFuture.completedFuture(response?.status == HttpStatusCode.OK)
    }

    companion object {
        val urlValidator = UrlValidator(arrayOf("http", "https"))
    }
}

/**
 * Implementation of the port [HashService].
 */
@Suppress("UnstableApiUsage")
class HashServiceImpl : HashService {
    override fun hasUrl(url: String) = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString()
}


