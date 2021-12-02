package es.unizar.urlshortener

import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.HashServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ReachableServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.CheckReachableServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.ClickEntityRepository
import es.unizar.urlshortener.infrastructure.repositories.ClickRepositoryServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.ShortUrlEntityRepository
import es.unizar.urlshortener.infrastructure.repositories.ShortUrlRepositoryServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.QrCodeEntityRepository
import es.unizar.urlshortener.infrastructure.repositories.QrCodeRepositoryServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */
@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository,
    @Autowired val qrCodeEntityRepository: QrCodeEntityRepository,
) {
    @Bean
    fun clickRepositoryService() = ClickRepositoryServiceImpl(clickEntityRepository)

    @Bean
    fun shortUrlRepositoryService() = ShortUrlRepositoryServiceImpl(shortUrlEntityRepository)

    @Bean
    fun qrCodeRepositoryService() = QrCodeRepositoryServiceImpl(qrCodeEntityRepository)

    @Bean
    fun validatorService() = ValidatorServiceImpl()

    @Bean
    fun hashService() = HashServiceImpl()
    
    @Bean
    fun checkReachableService() = CheckReachableServiceImpl()

    @Bean
    fun reachableService() = ReachableServiceImpl()

    @Bean
    fun reachableService() = ReachableServiceImpl()

    @Bean
    fun redirectUseCase() = RedirectUseCaseImpl(shortUrlRepositoryService())

    @Bean
    fun logClickUseCase() = LogClickUseCaseImpl(clickRepositoryService())

    @Bean
    fun createQrCodeUseCase() = CreateQrCodeUseCaseImpl(qrCodeRepositoryService())

    @Bean
    fun getQrImageUseCase() = GetQrImageUseCaseImpl(qrCodeRepositoryService())

    @Bean
    fun getClicksNumberUseCase() = GetClicksNumberUseCaseImpl(clickRepositoryService())

    @Bean
    fun getClicksDayUseCase() = GetClicksDayUseCaseImpl(clickRepositoryService())

    @Bean
    fun getUsersCountUseCase() = GetUsersCountUseCaseImpl(clickRepositoryService())
    
    @Bean
    fun createShortUrlUseCase() = CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), validatorService(), hashService(), reachableService())

    
    @Bean
    fun limitRedirectUseCase() = LimitRedirectUseCaseImpl(clickRepositoryService())
}