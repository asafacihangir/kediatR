package com.trendyol.kediatr.spring

import com.trendyol.kediatr.*

class SpringCommandBus(
    private val springBeanRegistry: SpringBeanRegistry,
    private val publishStrategy: PublishStrategy = StopOnExceptionPublishStrategy()
) : CommandBus {

    override fun <TCommand : Command> executeCommand(command: TCommand) =
        springBeanRegistry.resolveCommandHandler(command.javaClass).handle(command)

    override suspend fun <TCommand : Command> executeCommandAsync(command: TCommand) =
        springBeanRegistry.resolveAsyncCommandHandler(command.javaClass).handleAsync(command)

    override fun <R, Q : Query<R>> executeQuery(query: Q): R =
        springBeanRegistry.resolveQueryHandler(query.javaClass).handle(query)

    override suspend fun <R, Q : Query<R>> executeQueryAsync(query: Q): R =
        springBeanRegistry.resolveAsyncQueryHandler(query.javaClass).handleAsync(query)

    override fun <T : Notification> publishNotification(notification: T) {
        publishStrategy.publish(notification, springBeanRegistry.resolveNotificationHandlers(notification.javaClass))
    }

    override suspend fun <T : Notification> publishNotificationAsync(notification: T) {
        publishStrategy.publishAsync(
            notification,
            springBeanRegistry.resolveAsyncNotificationHandlers(notification.javaClass)
        )
    }
}