package io.github.blackbaroness.vk

import io.github.blackbaroness.vk.model.method.GroupsGetLongPollServer
import io.github.blackbaroness.vk.model.method.GroupsGetLongPollSettings
import io.github.blackbaroness.vk.model.method.GroupsSetLongPollSettings
import io.github.blackbaroness.vk.model.method.MessagesSend
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.Closeable

class VkClient(val token: String) : Closeable {

    val groups = Groups()
    val messages = Messages()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    override fun close() {
        client.close()
        runBlocking { client.coroutineContext.job.join() }
    }

    suspend fun <RESULT, METHOD : VkMethod<RESULT>> execute(method: METHOD, configurator: METHOD.() -> Unit): RESULT {
        configurator.invoke(method)

        return try {
            val request = client.request(method.customUrl ?: "https://api.vk.com/method/${method.name}") {
                this.method = method.httpMethod
                parameter("access_token", token)
                parameter("v", "5.199")
                method.parameters.forEach { (key, value) -> parameter(key, value) }
            }

            request.body(method.resultTypeInfo)
        } catch (e: ResponseException) {
            val body = e.response.bodyAsText()
            val url = e.response.request.url
            val status = e.response.status
            throw RuntimeException("VK API error: $status\nURL: $url\nResponse body:\n$body", e)
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
            configure: GroupsGetLongPollServer.() -> Unit,
        ): GroupsGetLongPollServer.Result {
            val method = GroupsGetLongPollServer()
            method.groupId = groupId
            return execute(method, configure)
        }

        suspend fun getGetLongPollSettings(
            groupId: Long,
            configure: GroupsGetLongPollSettings.() -> Unit,
        ): GroupsGetLongPollSettings.Result {
            val method = GroupsGetLongPollSettings()
            method.groupId = groupId
            return execute(method, configure)
        }

        suspend fun setLongPollSettings(groupId: Long, configure: GroupsSetLongPollSettings.() -> Unit) {
            val method = GroupsSetLongPollSettings()
            method.groupId = groupId
            execute(method, configure)
        }
    }

    inner class Messages {

        suspend fun send(userId: Long, configure: MessagesSend.() -> Unit) {
            val method = MessagesSend()
            method.userId = userId
            execute(method, configure)
        }
    }
}