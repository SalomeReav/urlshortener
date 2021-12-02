package es.unizar.urlshortener.core.usecases


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
    private val hashService: HashService,
    private val reachableService: ReachableService
) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties): ShortUrl =
        if (validatorService.isValid(url)) {
// no puedo hacer las cosas aqui tengo que coger el ejemplo de validatorService(implementado en delivery
//) además para hacerlo asincrono en kotlin hay corrutinas y no se donde algo se asinc
//lo primero que hay que hacer es q pase lo que se cree el link acortado y despues ya el validador se encargue de decir si se puede usar o no
/*val client = HttpClient.newBuilder().build();
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
            
        val response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200){
            throw InvalidUrlException(url)
        }
//nuevo*/
            if (reachableService.isReachable(url)){
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
            } else throw NotReachableUrlException(url)
        } else {
            throw InvalidUrlException(url)
        }
}
