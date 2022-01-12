package es.unizar.urlshortener.core

class InvalidUrlException(val url: String) : Exception("[$url] does not follow a supported schema")

class RedirectionNotFound(val key: String) : Exception("[$key] is not known")

class QrCodeNotFound(val key: String) : Exception("[$key] is not known")

class QrCodeNotCreated(val url: String) : Exception("qrCode for [$url] has not yet been created ")

class UnavailableUrl(val key: String) : Exception("[$key] is unavailable")

class UrlNotReachable(val url: String) : Exception("[$url] is not reachable")

class UrlNotChecked(val url: String) : Exception("[$url] has not been checked")