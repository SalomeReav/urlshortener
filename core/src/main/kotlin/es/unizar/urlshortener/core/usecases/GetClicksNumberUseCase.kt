package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 *  Returns the clicks number from a url identified by it [id]
 */
interface GetClicksNumberUseCase {
    fun getClicksNumber(hash: String, remoteUsr: String) : Int
}

/**
 * Implementation of [GetClicksNumberUseCase].
 */
class GetClicksNumberUseCaseImpl (
    private val clickRepository: ClickRepositoryService
    ) : GetClicksNumberUseCase {
        override fun getClicksNumber(hash: String, remoteUsr: String): Int {
            var lastRemoteUser = clickRepository.findByHash(hash).last() //Get the lastRemoteUser who did a click
            if (remoteUsr == lastRemoteUser.properties.lastRemoteUser) return 0 //Avoid that user generate statistics on my url on each consecutive click.
            val numClick = clickRepository.countByHash(hash)
            return numClick
        }
    }