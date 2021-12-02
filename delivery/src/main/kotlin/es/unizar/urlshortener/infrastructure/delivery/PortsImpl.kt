package es.unizar.urlshortener.infrastructure.delivery

import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.HashService
import es.unizar.urlshortener.core.ReachableService
import es.unizar.urlshortener.core.ValidatorService
import org.apache.commons.validator.routines.UrlValidator
import java.nio.charset.StandardCharsets
import es.unizar.urlshortener.core.CheckReachableService
import es.unizar.urlshortener.core.NonReachableUrlException
import java.util.Date
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URL
import java.net.URLEncoder
import java.net.HttpURLConnection

/**
 * Implementation of the port [ValidatorService].
 */
class ValidatorServiceImpl : ValidatorService {
    override fun isValid(url: String) = urlValidator.isValid(url)

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

/**
<<<<<<< HEAD
 * Implementation of the port [CheckReachableService].
 */
@Suppress("UnstableApiUsage")
class CheckReachableServiceImpl : CheckReachableService {
    override fun isReachable(url: String):Boolean {
        try {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return (200 == response.statusCode())
        } catch (e:Exception){
            throw NonReachableUrlException(url)
        }
    }
}
=======
  * Implementation of the port [ReachableService].
  */
  class ReachableServiceImpl : ReachableService {
    override fun isReachable(url: String): Boolean {
        val auxUrl: URL = URL(url)
        val connection: HttpURLConnection = auxUrl.openConnection() as HttpURLConnection
        connection.setConnectTimeout(5000)
        connection.connect()
        val code = connection.getResponseCode()
        println(code)

        if (code == 200) { 
            return true
        } else{
            return false
        }
    }
 }
>>>>>>> 8bceb20 (check if reacheable)
