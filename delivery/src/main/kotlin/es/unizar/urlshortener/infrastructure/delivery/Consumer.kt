package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.usecases.CreateQrCodeUseCase
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

class LogConsumer(private val queue: BlockingQueue<String>, private val qrService: CreateQrCodeUseCase) : Runnable {
    override fun run() {
        try {
            runBlocking {
                while (true) {
                    coroutineScope {  // this: CoroutineScope
                        val url = queue.take()
                        launch() {
                            println("Start " + url)
                            Thread.sleep(2500)
                            qrService.createQRImage(URI(url))
                        }
                    }
                }
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}