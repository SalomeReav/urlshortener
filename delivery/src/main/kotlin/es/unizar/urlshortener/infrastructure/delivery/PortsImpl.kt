package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.scheduling.annotation.Async
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

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
    open override fun isReachable(url: String): CompletableFuture<Boolean> {
        val response: HttpResponse?
        runBlocking {
            response = try {
                client.get(url)
            } catch (e: Exception) {
                null
            }
        }

        return CompletableFuture.completedFuture(response?.status == HttpStatusCode.OK)
    }

    @Async("taskExecutorSafe")
    open override fun checkUrlSafe(url : String) : CompletableFuture<Boolean> {
         val apiUrl =
            "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyDd8lcmHyHjn6hi3DoFgNVn2Exs4nk1oYM"
        val headers = mapOf(
            "Content-Type" to "application/json"
        )
        val data = """
            { "client": 
                {
                    "clientId": "urlshortener-d",
                    "clientVersion": "1.5.2"
                },
            "threatInfo": {
                "threatTypes": ["MALWARE", "SOCIAL_ENGINEERING"],
                "platformTypes": ["WINDOWS"],
                "threatEntryTypes": ["URL"],
                "threatEntries": [{"url": "$url"},]
            }
        }""".trimIndent()

        var responseBody = "{}"
        runBlocking {
            val connection = URL(apiUrl).openConnection() as HttpURLConnection
            with(connection) {
                requestMethod = "POST"
                doOutput = (data != "{}")
                headers?.forEach(this::setRequestProperty)
            }

            if (data != null) {
                connection.outputStream.use {
                    it.write(data.toByteArray())
                }
            }
            responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
            println("RESPONSE--"+responseBody)
        }

        return CompletableFuture.completedFuture(responseBody == "{}")
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


