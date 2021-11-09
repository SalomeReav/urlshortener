package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import java.util.Date
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
/**
 * Given a shorted url returns if it's reachable or not.
 *
 *
 * **Note**: This is an example of functionality.
 */
interface CheckShortUrlUseCase {
    fun check(url: String) : Boolean
}

/**
 * Implementation of [CheckShortUrlUseCase].
 */
class CheckShortUrlUseCaseImpl(

) : CheckShortUrlUseCase{
    override fun check(url: String) : Boolean{
        val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
            
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return (200 == response.statusCode())
    }
}
