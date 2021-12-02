package es.unizar.urlshortener.infrastructure.delivery
import es.unizar.urlshortener.core.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.net.URI
import boofcv.alg.fiducial.qrcode.QrCodeEncoder
import boofcv.alg.fiducial.qrcode.QrCodeGeneratorImage
import com.google.common.hash.Hashing
import es.unizar.urlshortener.core.usecases.*
import java.nio.charset.StandardCharsets
@WebMvcTest
@ContextConfiguration(classes = [
    UrlShortenerControllerImpl::class,
    RestResponseEntityExceptionHandler::class])
class UrlShortenerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var redirectUseCase: RedirectUseCase

    @MockBean
    private lateinit var logClickUseCase: LogClickUseCase

    @MockBean
    private lateinit var createShortUrlUseCase: CreateShortUrlUseCase

    @MockBean
    private lateinit var createQrCodeUseCase: CreateQrCodeUseCase

    @MockBean
    private lateinit var getQrImageUseCase: GetQrImageUseCase

    @MockBean
    private lateinit var getClicksNumberUseCase: GetClicksNumberUseCase

    @MockBean
    private lateinit var getClicksDayUseCase: GetClicksDayUseCase

    @MockBean
    private lateinit var getUsersCountUseCase: GetUsersCountUseCase

    @MockBean
    private lateinit var limitRedirectUseCase: LimitRedirectUseCase

    @Test
    fun `redirectTo returns a redirect when the key exists`() {
        given(limitRedirectUseCase.limitRedirectByDay("key")).willReturn(true)
        given(redirectUseCase.redirectTo("key")).willReturn(Redirection("http://example.com/"))

        mockMvc.perform(get("/tiny-{id}", "key"))
            .andExpect(status().isTemporaryRedirect)
            .andExpect(redirectedUrl("http://example.com/"))

        verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `redirectTo returns a not found when the key does not exist`() {
        given(limitRedirectUseCase.limitRedirectByDay("key")).willReturn(true)
        given(redirectUseCase.redirectTo("key"))
            .willAnswer { throw RedirectionNotFound("key") }

        mockMvc.perform(get("/tiny-{id}", "key"))
            .andDo(print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.statusCode").value(404))

        verify(logClickUseCase, never()).logClick("key", ClickProperties(ip = "127.0.0.1"))
    }

    @Test
    fun `redirectTo returns a service unavailable when the key exists but the limit has been reached`() {
        given(limitRedirectUseCase.limitRedirectByDay("key")).willReturn(false)
        mockMvc.perform(get("/tiny-{id}", "key"))
            .andExpect(status().isServiceUnavailable)
            .andExpect(jsonPath("$.statusCode").value(503))
    }

    @Test
    fun `creates returns a basic redirect if it can compute a hash`() {
        given(createShortUrlUseCase.create(
            url = "http://example.com/",
            data = ShortUrlProperties(ip = "127.0.0.1")
        )).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/")))

        mockMvc.perform(post("/api/link")
            .param("url", "http://example.com/")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(redirectedUrl("http://localhost/tiny-f684a3c4"))
            .andExpect(jsonPath("$.url").value("http://localhost/tiny-f684a3c4"))
    }

    @Test
    fun `creates returns bad request if it can compute a hash`() {
        given(createShortUrlUseCase.create(
            url = "ftp://example.com/",
            data = ShortUrlProperties(ip = "127.0.0.1")
        )).willAnswer { throw InvalidUrlException( "ftp://example.com/") }

        mockMvc.perform(post("/api/link")
            .param("url", "ftp://example.com/")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.statusCode").value(400))
    }

    @Test
    fun `creates returns bad request if it url not reachable`() {
        given(createShortUrlUseCase.create(
            url = "http://notreachableurl.com/",
            data = ShortUrlProperties(ip = "127.0.0.1")
        )).willAnswer { throw NotReachableUrlException ("http://notreachableurl.com/") }

        mockMvc.perform(post("/api/link") 
            .param("url", "http://notreachableurl.com/")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andExpect(status().isBadRequest)
            
    } 


    @Test
    fun `clicksInfo returns a json with clicks, users and clicksByDay when the key exists`() {
        given(getClicksNumberUseCase.getClicksNumber("key")).willReturn(0)
        given(getUsersCountUseCase.getUsersCount("key")).willReturn(0)
        given(getClicksDayUseCase.getClicksDay("key")).willReturn(mutableMapOf<String,Int>())
        mockMvc.perform(get("/{id}.json", "key"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.clicks").value(0))
            .andExpect(jsonPath("$.users").value(0))
            .andExpect(jsonPath("$.clicksByDay").value(""))
    }

    @Test
    fun `creates returns a qrCode url if specified `(){
        given(createShortUrlUseCase.create(
            url = "http://www.unizar.es/",
            data = ShortUrlProperties(ip = "127.0.0.1")
        )).willReturn(ShortUrl("6bb9db44", Redirection("http://www.unizar.es/")))
        given(createQrCodeUseCase.create(
            url = URI.create("http://localhost/tiny-6bb9db44"),
        )).willReturn(qrCode(URI.create("http://localhost/tiny-6bb9db44")))

        mockMvc.perform(post("/api/link")
            .param("url", "http://www.unizar.es/")
            .param("createQR", "true")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(redirectedUrl("http://localhost/tiny-6bb9db44"))
            .andExpect(jsonPath("$.url").value("http://localhost/tiny-6bb9db44"))
            .andExpect(jsonPath("$.qr").value("http://localhost/qr/6bb9db44"))
    }

     @Test
    fun `getQrImage returns a image when the key exists`() {
        given(getQrImageUseCase.getQrImage("key")
        ).willReturn(qrCode(URI.create("http://localhost/tiny-6bb9db44")))

        mockMvc.perform(get("/qr/{id}", "key"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
    }

    @Test
    fun `getQrImage returns a not found when the key does not exist`() {
        given(getQrImageUseCase.getQrImage("key"))
            .willAnswer { throw QrCodeNotFound("key") }
        mockMvc.perform(get("/qr/{id}", "key"))
            .andDo(print())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.statusCode").value(404))
    }

    private fun qrCode(url: URI): QrCode {
        val qr = QrCodeEncoder().addAutomatic(url.toString()).fixate()
        val generator = QrCodeGeneratorImage(15).render(qr)
        val urlString = url.toString()
        val id: String = urlString.substring(urlString.lastIndexOf("-")+1)
        return QrCode(
            hash = id,
            gray = generator.gray,
        )
    }
}