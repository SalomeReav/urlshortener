package es.unizar.urlshortener.infrastructure.repositories

import java.time.OffsetDateTime
import javax.persistence.*

/**
 * The [ClickEntity] entity logs clicks.
 */
@Entity
@Table(name = "click")
class ClickEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,
    val hash: String,
    val created: OffsetDateTime,
    val ip: String?,
    val referrer: String?,
    val browser: String?,
    val platform: String?,
    val country: String?
)

/**
 * The [ShortUrlEntity] entity stores short urls.
 */
@Entity
@Table(name = "shorturl")
class ShortUrlEntity(
    @Id
    val hash: String,
    val target: String,
    val sponsor: String?,
    val created: OffsetDateTime,
    val owner: String?,
    val mode: Int,
    val safe: Boolean,
    val checkedSafe: Boolean,
    val checked: Boolean,
    val reachable: Boolean,
    val ip: String?,
    val country: String?,
    val redirectCount: Int?,
    val lastRedirect: OffsetDateTime?
)

/**
 * The [QrCodeEntity] entity stores qrcode image.
 */
@Entity
@Table(name = "qrcode")
class QrCodeEntity(
    @Id
    val hash: String,
    val url: String,
    @Column(name = "image", length = Integer.MAX_VALUE)
    var image: ByteArray?
)