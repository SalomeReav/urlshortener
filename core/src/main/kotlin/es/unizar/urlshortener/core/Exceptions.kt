package es.unizar.urlshortener.core

class InvalidUrlException(val url: String) : Exception("[$url] does not follow a supported schema")

<<<<<<< HEAD
class NonReachableUrlException(val url: String) : Exception("[$url] is not reachable")
=======

class NotReachableUrlException(val url: String) : Exception("[$url] does not return 200 code")
<<<<<<< HEAD
>>>>>>> 8bceb20 (check if reacheable)
=======
>>>>>>> 8bceb20485ddd9776bb806a52e35f8e8b42368ba
 
class RedirectionNotFound(val key: String) : Exception("[$key] is not known")

class QrCodeNotFound(val key: String) : Exception("[$key] is not known")

class UnavailableUrl(val key: String) : Exception("[$key] is unavailable")

