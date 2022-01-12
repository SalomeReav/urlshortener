package es.unizar.urlshortener

import es.unizar.urlshortener.core.usecases.*
import es.unizar.urlshortener.infrastructure.delivery.HashServiceImpl
import es.unizar.urlshortener.infrastructure.delivery.ValidatorServiceImpl
import es.unizar.urlshortener.infrastructure.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor


/**
 * Wires use cases with service implementations, and services implementations with repositories.
 *
 * **Note**: Spring Boot is able to discover this [Configuration] without further configuration.
 */
/*org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'taskExecutorReachable' available: No matching Executor bean found for qualifier 'taskExecutorReachable' - neither qualifier match nor bean name match! */

/* Information about asyncExecutor : https://howtodoinjava.com/spring-boot2/rest/enableasync-async-controller/
* */
@EnableAsync(proxyTargetClass = true)
@Configuration
class ApplicationConfiguration(
    @Autowired val shortUrlEntityRepository: ShortUrlEntityRepository,
    @Autowired val clickEntityRepository: ClickEntityRepository,
    @Autowired val qrCodeEntityRepository: QrCodeEntityRepository,
) {

    @Bean(name = ["taskExecutorUriInformation"])
    fun executorTask(): Executor? {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 10
        executor.setQueueCapacity(150)
        executor.initialize()
        return executor
    }

    @Bean(name = ["taskExecutorReachable"])
    fun taskExecutor(): Executor? {
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
        return ArrayBlockingQueue<String>(10)
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
    fun createShortUrlUseCase() =
        CreateShortUrlUseCaseImpl(shortUrlRepositoryService(), validatorService(), hashService())

    @Bean
    fun limitRedirectUseCase() = LimitRedirectUseCaseImpl(shortUrlRepositoryService())
}