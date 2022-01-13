package es.unizar.urlshortener.core

import java.time.OffsetDateTime
import java.util.*

/**
 * A [Click] captures a request of redirection of a [ShortUrl] identified by its [hash].
 */
data class Click(
    val hash: String,
    val properties: ClickProperties = ClickProperties(),
    val created: OffsetDateTime = OffsetDateTime.now()
)

/**
 * A [ShortUrl] is the mapping between a remote url identified by [redirection] and a local short url identified by [hash].
 */
data class ShortUrl(
    val hash: String,
    val redirection: Redirection,
    val created: OffsetDateTime = OffsetDateTime.now(),
    val properties: ShortUrlProperties = ShortUrlProperties(),
    var redirectCount: Int? = 0,
    var lastRedirect: OffsetDateTime? = OffsetDateTime.now(),
    var clicksInfo: ClicksInfo = ClicksInfo(),
    var lastInfo: OffsetDateTime? = OffsetDateTime.now(),
)

/**
 * A [QRCode] contains a qrcode image [image] identified by [hash].
 */
data class QrCode(
    val hash: String,
    var image: ByteArray? = null,
    val url: String,
)

/**
 * A [TimeOfRedirection] contains the moment [last] of the last redirection
 * identified by [hash] .
 */
data class TimeOfRedirection(
    val hash: String,
    val last: OffsetDateTime,
)

/**
 * A [Redirection] specifies the [target] and the [status code][mode] of a redirection.
 * By default, the [status code][mode] is 307 TEMPORARY REDIRECT.
 */
data class Redirection(
    val target: String,
    val mode: Int = 307
)

/**
 * A [ShortUrlProperties] is the bag of properties that a [ShortUrl] may have.
 */
data class ShortUrlProperties(
    val ip: String? = null,
    val sponsor: String? = null,
    var safe: Boolean = true,
    var checkedSafe: Boolean = false,
    var checked: Boolean = false,
    var reachable: Boolean = false,
    val owner: String? = null,
    val country: String? = null,
)

/**
 * A [ClickProperties] is the bag of properties that a [Click] may have.
 */
data class ClickProperties(
    val ip: String? = null,
    val referrer: String? = null,
    val browser: String? = null,
    val platform: String? = null,
    val lastRemoteUser: String? = null,
    val country: String? = null,
)

/**
 * A [ClicksInfo] is the bag of stats of [Click]s that a [ShortUrl] may have.
 */
data class ClicksInfo(
    var totalClicks: Int = 0,
    var usersCount: Int = 0,
    var numClicksDay: Map<String, Int> = emptyMap()
)
