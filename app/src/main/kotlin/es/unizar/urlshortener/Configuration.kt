package es.unizar.urlshortener

import es.unizar.urlshortener.core.TimeOfRedirection
import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.HashServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue

/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */

@EnableAsync(proxyTargetClass = true)
@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository,
    @Autowired val qrCodeEntityRepository: QrCodeEntityRepository,
) {

    @Bean(name = ["taskExecutorReachable"])
    fun reachableExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

    @Bean(name = ["taskExecutorQrCode"])
    fun qrCodeExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

    @Bean
    fun qrQueue(): BlockingQueue<String>? {
        return LinkedBlockingQueue<String>(10)
    }

    @Bean(name = ["taskExecutorLimit"])
    fun limitExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

    @Bean
    fun limitQueue(): BlockingQueue<TimeOfRedirection>? {
        return LinkedBlockingQueue<TimeOfRedirection>(10)
    }

    @Bean(name = ["taskExecutorSafe"])
    fun safeExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

    @Bean(name = ["taskExecutorClicks"])
    fun clicksExecutor(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

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
    fun redirectUseCase() = RedirectUseCaseImpl(shortUrlRepositoryService())

    @Bean
    fun logClickUseCase() =
        LogClickUseCaseImpl(clickRepositoryService(), getClicksInfoUseCase(), shortUrlRepositoryService())

    @Bean
    fun createQrCodeUseCase() = CreateQrCodeUseCaseImpl(qrCodeRepositoryService())

    @Bean
    fun getQrImageUseCase() = GetQrImageUseCaseImpl(qrCodeRepositoryService())

    @Bean
    fun getClicksInfoUseCase() = GetClicksInfoUseCaseImpl(clickRepositoryService(), shortUrlRepositoryService())

    @Bean
    fun createShortUrlUseCase() =
        CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), validatorService(), hashService())

    @Bean
    fun limitRedirectUseCase() = LimitRedirectUseCaseImpl(shortUrlRepositoryService())
}