package io.github.blackbaroness.vk

import io.github.blackbaroness.vk.model.exception.GenericVkException
import io.github.blackbaroness.vk.model.method.GetUpdatesVkMethod
import io.github.blackbaroness.vk.model.method.groups.GroupsGetLongPollServerVkMethod
import io.github.blackbaroness.vk.model.method.groups.GroupsGetLongPollSettingsVkMethod
import io.github.blackbaroness.vk.model.method.groups.GroupsIsMemberVkMethod
import io.github.blackbaroness.vk.model.method.groups.GroupsSetLongPollSettingsVkMethod
import io.github.blackbaroness.vk.model.method.messages.MessagesEditVkMethod
import io.github.blackbaroness.vk.model.method.messages.MessagesSendVkMethod
import io.github.blackbaroness.vk.model.method.users.UsersGetVkMethod
import io.github.blackbaroness.vk.model.`object`.User
import io.github.blackbaroness.vk.model.response.Ok
import io.github.blackbaroness.vk.model.response.VkResponse
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import org.slf4j.Logger
import java.io.Closeable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class VkClient(val token: String, clientFactory: HttpClientEngineFactory<*>) : Closeable {

    val groups = Groups()
    val messages = Messages()
    val users = Users()

    val client = HttpClient(clientFactory) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5_000
        }
    }

    override fun close() {
        client.close()
        runBlocking { client.coroutineContext.job.join() }
    }

    suspend fun <RESULT, METHOD : VkMethod<RESULT>> execute(
        method: METHOD,
        clientConfigurator: HttpRequestBuilder.() -> Unit = {}
    ): RESULT {
        try {
            val request = client.request(method.customUrl ?: "https://api.vk.com/method/${method.name}") {
                this.method = method.httpMethod
                parameter("access_token", token)
                parameter("v", "5.199")
                method.parameters.forEach { (key, value) -> parameter(key, value) }

                clientConfigurator()
            }

            val response = try {
                json.decodeFromString(VkResponse.serializer(method.resultSerializer), request.bodyAsText())
            } catch (e: Throwable) {
                throw RuntimeException("Error decoding VK answer: ${request.bodyAsText()}", e)
            }

            if (response.error != null) {
                throw GenericVkException(response.error.code, response.error.message)
            }

            if (response.response == null) {
                throw RuntimeException("Null response! Full response: '$response', url: '${request.request.url}', answer: '${request.bodyAsText()}'")
            }

            return response.response
        } catch (e: ResponseException) {
            val body = e.response.bodyAsText()
            val url = e.response.request.url
            val status = e.response.status
            throw RuntimeException("VK API error: $status\nURL: $url\nResponse body:\n$body", e)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun startLongPolling(communityId: Long, logger: Logger?): Flow<GetUpdatesVkMethod.Result.Update> = flow {
        var server: String? = null
        var key: String? = null
        var ts: String? = null
        var nextCacheReset = Instant.DISTANT_PAST

        suspend fun ensureLongPollServer() {
            if (server == null || key == null || ts == null || Clock.System.now() > nextCacheReset) {
                val serverInfo = groups.getLongPollServer(communityId)
                server = serverInfo.server
                key = serverInfo.key
                ts = serverInfo.ts
                nextCacheReset = Clock.System.now() + 5.minutes
            }
        }

        suspend fun poll() {
            val method = GetUpdatesVkMethod().apply {
                this.server = server!!
                this.key = key!!
                this.ts = ts!!
            }

            val response = execute(method) {
                timeout {
                    requestTimeoutMillis = (method.wait.seconds + 5.seconds).inWholeMilliseconds
                }
            }

            ts = response.ts

            response.updates.forEach { emit(it) }
        }

        fun handleVkError(t: Throwable): Boolean {
            val json = t.message
                ?.let { runCatching { json.parseToJsonElement(it) }.getOrNull() }
                as? JsonObject
                ?: return false

            val failed = (json["failed"] as? JsonPrimitive)?.intOrNull ?: return false

            return when (failed) {
                1 -> {
                    ts = json["ts"]?.jsonPrimitive?.content
                    true
                }

                2, 3 -> {
                    nextCacheReset = Instant.DISTANT_PAST
                    true
                }

                else -> false
            }
        }

        while (currentCoroutineContext().isActive) {
            try {
                while (currentCoroutineContext().isActive) {
                    ensureLongPollServer()
                    poll()
                }
            } catch (t: Throwable) {
                try {
                    if (!handleVkError(t)) {
                        logger?.error("Failed to get updates from VK", t)
                        delay(5.seconds)
                    }
                } catch (handleThrowable: Throwable) {
                    handleThrowable.addSuppressed(t)
                    logger?.error("Error handling VK error", handleThrowable)
                    delay(5.seconds)
                }
            }
        }
    }

    companion object {
        val json = Json {
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    inner class Groups {

        suspend fun getLongPollServer(
            groupId: Long,
            configure: GroupsGetLongPollServerVkMethod.() -> Unit = {},
        ): GroupsGetLongPollServerVkMethod.Result {
            val method = GroupsGetLongPollServerVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend fun getGetLongPollSettings(
            groupId: Long,
            configure: GroupsGetLongPollSettingsVkMethod.() -> Unit = {},
        ): GroupsGetLongPollSettingsVkMethod.Result {
            val method = GroupsGetLongPollSettingsVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend fun setLongPollSettings(
            groupId: Long,
            configure: GroupsSetLongPollSettingsVkMethod.() -> Unit = {}
        ): Int {
            val method = GroupsSetLongPollSettingsVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend fun isMember(groupId: String, userId: Long, configure: GroupsIsMemberVkMethod.() -> Unit = {}): Ok {
            val method = GroupsIsMemberVkMethod()
            method.groupId = groupId
            method.userId = userId
            return execute(method.apply(configure))
        }
    }

    inner class Messages {

        suspend fun send(userId: Long, configure: MessagesSendVkMethod.() -> Unit = {}): MessagesSendVkMethod.Result {
            val method = MessagesSendVkMethod()
            method.userId = userId
            return execute(method.apply(configure))
        }

        suspend fun edit(peerId: Long, message: String, configure: MessagesEditVkMethod.() -> Unit = {}): Ok {
            val method = MessagesEditVkMethod()
            method.peerId = peerId
            method.message = message
            return execute(method.apply(configure))
        }
    }

    inner class Users {

        suspend fun get(userId: Long, configure: UsersGetVkMethod.() -> Unit = {}): User {
            val method = UsersGetVkMethod()
            method.userIds = userId.toString()
            return execute(method.apply(configure)).single()
        }
    }
}
