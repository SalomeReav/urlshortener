package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 *  Returns the clicks number from a url identified by it [id]
 */
interface GetClicksNumberUseCase {
    fun getClicksNumber(hash: String) : Int
}

/**
 * Implementation of [GetClicksNumberUseCase].
 */
class GetClicksNumberUseCaseImpl (
    private val clickRepository: ClickRepositoryService
    ) : GetClicksNumberUseCase {
        override fun getClicksNumber(hash: String): Int {
            val numClick = clickRepository.countByHash(hash)
            return numClick
        }
    }