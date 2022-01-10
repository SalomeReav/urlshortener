package es.unizar.urlshortener.infrastructure.delivery.gateway;
import es.unizar.urlshortener.core.CheckURLSafeService
import java.net.HttpURLConnection
import java.net.URL
class CheckURLSafeServiceImpl(

): CheckURLSafeService {
    override fun checkUrlSafe(url: String): Boolean {
        
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

        val connection = URL(apiUrl).openConnection() as HttpURLConnection
        with(connection) {
            requestMethod = "POST"
            doOutput = data != null
            headers?.forEach(this::setRequestProperty)
        }

        if (data != null) {
            connection.outputStream.use {
                it.write(data.toByteArray())
            }
        }
        val responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
        println("-----------"+responseBody)
        return true;


        /*if( JSON.stringify(respuesta) != '{}'){
        throw InvalidUrlException(url)
    }
    return (200 == response.statusCode())*/

    }
}
