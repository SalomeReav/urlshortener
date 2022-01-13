package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ValidatorService
import io.ktor.client.*
import io.ktor.client.call.*
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
        val data = """
            { "client": 
                {
                    "clientId": "urlshortener-d",
                    "clientVersion": "1.5.2"
                },
            "threatInfo": {
				"threatTypes": ["MALWARE", "SOCIAL_ENGINEERING", "THREAT_TYPE_UNSPECIFIED",
                                "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"],
				"platformTypes": ["WINDOWS"],
                "threatEntryTypes": ["URL"],
                "threatEntries": [{"url": "$url"},]
            }
        }""".trimIndent()
        val responseBody: String
        runBlocking {
            val httpResponse: HttpResponse = client.post(apiUrl) {
                contentType(ContentType.Application.Json)
                body = data
                headers{
                    append(HttpHeaders.Accept, "text/html")
                }
            }
            println("STATUS"+httpResponse.toString())
            responseBody = httpResponse.receive()
        }
        println("RESPONSE2"+"$url"+"--"+responseBody+"-|||-"+(responseBody=="{}\n"))
        return CompletableFuture.completedFuture(responseBody == "{}\n")
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


