package es.unizar.urlshortener.infrastructure.repositories

import es.unizar.urlshortener.core.*

/**
 * Extension method to convert a [ClickEntity] into a domain [Click].
 */
fun ClickEntity.toDomain() = Click(
    hash = hash,
    created = created,
    properties = ClickProperties(
        ip = ip,
        referrer = referrer,
        browser = browser,
        platform = platform,
        country = country,
        lastRemoteUser = lastRemoteUser
    )
)

/**
 * Extension method to convert a domain [Click] into a [ClickEntity].
 */
fun Click.toEntity() = ClickEntity(
    id = null,
    hash = hash,
    created = created,
    ip = properties.ip,
    referrer = properties.referrer,
    browser = properties.browser,
    platform = properties.platform,
    country = properties.country,
    lastRemoteUser = properties.lastRemoteUser
)

/**
 * Extension method to convert a [ShortUrlEntity] into a domain [ShortUrl].
 */
fun ShortUrlEntity.toDomain() = ShortUrl(
    hash = hash,
    redirection = Redirection(
        target = target,
        mode = mode
    ),
    created = created,
    lastRedirect = lastRedirect,
    redirectCount = redirectCount,
    properties = ShortUrlProperties(
        sponsor = sponsor,
        owner = owner,
        safe = safe,
        checkedSafe = checkedSafe,
        checked = checked,
        reachable = reachable,
        ip = ip,
        country = country
    ),
    clicksInfo = ClicksInfo(
        numClicksDay = numClicksDay,
        totalClicks = totalClicks,
        usersCount = usersCount
    ),
    lastInfo = lastInfo
)

/**
 * Extension method to convert a domain [ShortUrl] into a [ShortUrlEntity].
 */
fun ShortUrl.toEntity() = ShortUrlEntity(
    hash = hash,
    target = redirection.target,
    mode = redirection.mode,
    created = created,
    lastRedirect = lastRedirect,
    redirectCount = redirectCount,
    owner = properties.owner,
    sponsor = properties.sponsor,
    safe = properties.safe,
    checkedSafe = properties.checkedSafe,
    checked = properties.checked,
    reachable = properties.reachable,
    ip = properties.ip,
    country = properties.country,
    numClicksDay = clicksInfo.numClicksDay,
    totalClicks = clicksInfo.totalClicks,
    usersCount = clicksInfo.usersCount,
    lastInfo = lastInfo
)

/**
 * Extension method to convert a [QrCodeEntity] into a domain [QrCode].
 */
fun QrCodeEntity.toDomain() = QrCode(
    hash = hash,
    image = image,
    url = url,
)

/**
 * Extension method to convert a domain [QrCode] into a [QrCodeEntity].
 */
fun QrCode.toEntity() = QrCodeEntity(
    hash = hash,
    url = url,
    image = image
)