package io.github.blackbaroness.vk

import io.github.blackbaroness.vk.model.exception.GenericVkException
import io.github.blackbaroness.vk.model.exception.LongPollVkException
import io.github.blackbaroness.vk.model.method.GetUpdatesVkMethod
import io.github.blackbaroness.vk.model.method.groups.*
import io.github.blackbaroness.vk.model.method.messages.MessagesEditVkMethod
import io.github.blackbaroness.vk.model.method.messages.MessagesSendVkMethod
import io.github.blackbaroness.vk.model.method.users.UsersGetVkMethod
import io.github.blackbaroness.vk.model.`object`.User
import io.github.blackbaroness.vk.model.response.Ok
import io.github.blackbaroness.vk.model.response.VkResponse
import io.github.blackbaroness.vk.model.response.VkResponseWithNoResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import java.io.Closeable
import kotlin.time.Duration.Companion.seconds

class VkClient(
    private val token: String,
    private val httpClient: HttpClient
) : Closeable {

    val groups = Groups()
    val messages = Messages()
    val users = Users()

    override fun close() {
        httpClient.close()
        runBlocking { httpClient.coroutineContext.job.join() }
    }

    suspend fun <RESULT, METHOD : VkMethod<RESULT>> execute(
        method: METHOD,
        clientConfigurator: HttpRequestBuilder.() -> Unit = {}
    ): RESULT {
        var urlBuilder: URLBuilder? = null
        var answer: String? = null
        var status: HttpStatusCode? = null

        try {
            val url = method.customUrl ?: "https://api.vk.ru/method/${method.name}"
            val httpResponse = httpClient.request(url) {
                urlBuilder = this.url
                this.method = method.httpMethod
                parameter("access_token", token)
                parameter("v", "5.199")
                method.parameters.forEach { (key, value) -> parameter(key, value) }

                clientConfigurator()
            }

            status = httpResponse.status
            answer = httpResponse.bodyAsText()

            return when (method.resultStyle) {
                VkMethod.ResultStyle.CUSTOM ->
                    json.decodeFromString(method.resultSerializer, answer)

                VkMethod.ResultStyle.OPTIONAL_ERROR_FIELD -> {
                    val response = json.decodeFromString<VkResponseWithNoResponse>(answer)
                    if (response.error != null) throw GenericVkException(response.error.code, response.error.message)
                    json.decodeFromString(method.resultSerializer, answer)
                }

                VkMethod.ResultStyle.WRAPPED_IN_RESPONSE -> {
                    val response = json.decodeFromString(VkResponse.serializer(method.resultSerializer), answer)
                    if (response.error != null) throw GenericVkException(response.error.code, response.error.message)
                    response.response ?: throw NullPointerException("wrappedResult.response")
                }
            }
        } catch (e: Throwable) {
            e.cancellationCause?.let { throw it }

            val url = urlBuilder?.clone()?.apply {
                for (name in listOf("access_token", "key")) {
                    val values = parameters.getAll(name) ?: continue
                    parameters.remove(name)
                    parameters.appendAll(name, values.map { value ->
                        val headLength = 3
                        val tailLength = 3
                        val shownLength = headLength + tailLength
                        if (value.length <= shownLength) {
                            "*".repeat(value.length)
                        } else {
                            val head = value.take(headLength)
                            val tail = value.takeLast(tailLength)
                            val body = "*".repeat(value.length - shownLength)
                            head + body + tail
                        }
                    })
                }
            }?.buildString()

            throw GenericVkException(method, url, status, answer, e)
        }
    }

    fun startLongPolling(communityId: Long, logger: Logger?): Flow<GetUpdatesVkMethod.Result.Update> = flow {
        var server: String? = null
        var key: String? = null
        var ts: String? = null

        suspend fun ensureLongPollServer() {
            if (server == null || key == null || ts == null) {
                val serverInfo = groups.getLongPollServer(communityId)
                server = serverInfo.server
                key = serverInfo.key

                if (ts == null)
                    ts = serverInfo.ts
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
                    socketTimeoutMillis = (method.wait.seconds + 5.seconds).inWholeMilliseconds
                    requestTimeoutMillis = (method.wait.seconds + 10.seconds).inWholeMilliseconds
                }
            }

            ts = response.ts

            response.updates.forEach { emit(it) }
        }

        fun handleVkError(throwable: Throwable): Boolean {
            val error = throwable.rootCause as? LongPollVkException ?: return false

            // https://dev.vk.ru/ru/api/bots-long-poll/getting-started#%D0%9E%D1%88%D0%B8%D0%B1%D0%BA%D0%B8
            return when (error.failed) {
                1 -> {
                    ts = error.ts
                    true
                }

                2 -> {
                    key = null
                    true
                }

                3 -> {
                    key = null
                    ts = null
                    true
                }

                else -> false
            }
        }

        var retryDelay = 5.seconds
        while (currentCoroutineContext().isActive) {
            try {
                ensureLongPollServer()
                poll()
                retryDelay = 5.seconds
            } catch (t: Throwable) {
                t.cancellationCause?.let { throw it }
                currentCoroutineContext().ensureActive()
                if (!handleVkError(t)) {
                    logger?.error("Failed to get updates from VK, waiting $retryDelay before retry", t)
                    delay(retryDelay)
                    retryDelay = (retryDelay * 2).coerceAtMost(60.seconds)
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
            explicitNulls = false
        }
    }

    inner class Groups {

        suspend inline fun getLongPollServer(
            groupId: Long,
            configure: GroupsGetLongPollServerVkMethod.() -> Unit = {},
        ): GroupsGetLongPollServerVkMethod.Result {
            val method = GroupsGetLongPollServerVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend inline fun getGetLongPollSettings(
            groupId: Long,
            configure: GroupsGetLongPollSettingsVkMethod.() -> Unit = {},
        ): GroupsGetLongPollSettingsVkMethod.Result {
            val method = GroupsGetLongPollSettingsVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend inline fun setLongPollSettings(
            groupId: Long,
            configure: GroupsSetLongPollSettingsVkMethod.() -> Unit = {}
        ): Int {
            val method = GroupsSetLongPollSettingsVkMethod()
            method.groupId = groupId
            return execute(method.apply(configure))
        }

        suspend inline fun isMember(
            groupId: String,
            userId: Long,
            configure: GroupsIsMemberVkMethod.() -> Unit = {}
        ): Ok {
            val method = GroupsIsMemberVkMethod()
            method.groupId = groupId
            method.userId = userId
            return execute(method.apply(configure))
        }

        suspend inline fun getById(configure: GroupsGetByIdVkMethod.() -> Unit = {}): GroupsGetByIdVkMethod.Result {
            return execute(GroupsGetByIdVkMethod().apply(configure))
        }
    }

    inner class Messages {

        suspend inline fun send(
            userId: Long,
            configure: MessagesSendVkMethod.() -> Unit = {}
        ): MessagesSendVkMethod.Result {
            val method = MessagesSendVkMethod()
            method.userId = userId
            return execute(method.apply(configure))
        }

        suspend inline fun edit(peerId: Long, message: String, configure: MessagesEditVkMethod.() -> Unit = {}): Ok {
            val method = MessagesEditVkMethod()
            method.peerId = peerId
            method.message = message
            return execute(method.apply(configure))
        }
    }

    inner class Users {

        suspend inline fun get(userId: Long, configure: UsersGetVkMethod.() -> Unit = {}): User {
            val method = UsersGetVkMethod()
            method.userIds = userId.toString()
            return execute(method.apply(configure)).single()
        }
    }

    private val Throwable.cancellationCause: CancellationException?
        get() {
            var throwable = this
            while (true) {
                if (throwable is CancellationException) return throwable
                throwable = throwable.cause ?: return null
            }
        }

    private val Throwable.rootCause: Throwable?
        get() {
            var rootCause: Throwable? = this
            while (rootCause?.cause != null) {
                rootCause = rootCause.cause
            }
            return rootCause
        }
}
