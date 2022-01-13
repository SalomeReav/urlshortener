package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.TimeOfRedirection
import es.unizar.urlshortener.core.usecases.CreateQrCodeUseCase
import es.unizar.urlshortener.core.usecases.LimitRedirectUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.util.concurrent.BlockingQueue

class QRConsumer(private val queue: BlockingQueue<String>, private val qrService: CreateQrCodeUseCase) : Runnable {
    override fun run() {
        try {
            while (true) {
                var url = queue.take()
                qrService.createQRImage(URI(url))
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}

class LimitConsumer(private val queue: BlockingQueue<TimeOfRedirection>, private val limitService: LimitRedirectUseCase) : Runnable {
    override fun run() {
        try {
            while (true) {
                var redirection = queue.take()
                limitService.updateLastRedirect(redirection.hash,redirection.last)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}