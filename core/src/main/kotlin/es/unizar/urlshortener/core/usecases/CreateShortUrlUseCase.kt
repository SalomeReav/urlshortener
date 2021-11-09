package es.unizar.urlshortener.core.usecases

//
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
//Nuevo
import es.unizar.urlshortener.core.*
import java.util.Date

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties): ShortUrl
}

/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService
) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties): ShortUrl =
        if (validatorService.isValid(url)) {
//
val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
            
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200){
            throw InvalidUrlException(url)
        }
//nuevo

            val id: String = hashService.hasUrl(url)
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor
                )
            )
            shortUrlRepository.save(su)
        } else {
            throw InvalidUrlException(url)
        }
}
