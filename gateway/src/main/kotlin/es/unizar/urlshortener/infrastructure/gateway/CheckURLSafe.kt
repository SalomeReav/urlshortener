package es.unizar.urlshortener.infraestructure.gateway
import es.unizar.urlshortener.core.*
import java.util.Date
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


interface CheckURLSafe {
    fun checkUrlSafe (url: String) : Boolean
}


class checkURLSafeImpl(

): CheckURLSafe{
    override fun checkUrlSafe(url: String) : Boolean{
        val respuesta
        val url2 = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=AIzaSyDbt-GA2ZnFEmwgxO7hPlV3MkWWdmg_180 HTTP/1.1"
        val data={
        "client": {
            "clientId":      "urlshortener-d",
            "clientVersion": "1.5.2"
            },
            "threatInfo": {
            "threatTypes":      ["MALWARE", "SOCIAL_ENGINEERING"],
            "platformTypes":    ["WINDOWS"],
            "threatEntryTypes": ["URL"],
            "threatEntries": [{"url": url},]
            }
        };

    val param={
        headers:{
            "Content-Type": "application/json"
        },
        body: this.data,
        method: "POST"
    };
    fetch(url2,param)
    .then ( response => { respuesta= response.json()})
    .catch(error => console.log(error))                
    
    if( JSON.stringify(respuesta) != '{}'){
        throw InvalidUrlException(url)
    }
    return (200 == response.statusCode())
}
