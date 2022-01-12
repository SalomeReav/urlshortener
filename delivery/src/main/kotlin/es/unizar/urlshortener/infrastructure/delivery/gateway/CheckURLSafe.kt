package es.unizar.urlshortener.infrastructure.delivery.gateway;
import es.unizar.urlshortener.core.CheckURLSafeService
import java.net.HttpURLConnection
import java.net.URL
import es.unizar.urlshortener.core.*
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Async


class CheckURLSafeServiceImpl(

): CheckURLSafeService {
    @Async("taskExecutorSafe")
    override fun checkUrlSafe(url: String): CompletableFuture<Boolean> {
        
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
            val responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
        }
        println("-----------"+responseBody)

        return CompletableFuture.completedFuture(
            if( responseBody != "{}"){
                throw InvalidUrlException(url)
            }
            else{
                return true;
            }
        )
       

    }
}
