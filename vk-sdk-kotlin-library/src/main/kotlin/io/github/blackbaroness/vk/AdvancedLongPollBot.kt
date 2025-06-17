package io.github.blackbaroness.vk

import io.github.blackbaroness.vk.VkClient
import io.github.blackbaroness.vk.model.method.GetUpdatesVkMethod
import io.github.blackbaroness.vk.model.method.GroupsGetLongPollServer
import io.github.blackbaroness.vk.model.method.GroupsGetLongPollSettings
import io.github.blackbaroness.vk.model.method.GroupsSetLongPollSettings
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.Logger
import java.io.Closeable
import java.time.Instant
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

abstract class AdvancedLongPollBot(
    private val client: VkClient,
    private val groupId: Long,
    private val logger: Logger?
) : Closeable {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var communityId: Long = -1
    private var server: String = ""
    private var key: String = ""
    private var ts: String = ""
    private var nextCacheReset = Instant.MIN

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    ensureLongPollServer()
                    while (isActive) {
                        poll()
                    }
                } catch (t: Throwable) {
                    if (!handleVkError(t)) {
                        logger?.error("Failed to get updates from VK", t)
                        delay(5.seconds)
                    }
                }
            }
        }
    }

    override fun close() {
        scope.cancel()
    }

    private suspend fun ensureLongPollServer() {
        val now = Instant.now()
        if (now <= nextCacheReset) return
        nextCacheReset = now.plus(5.minutes.toJavaDuration())

        val serverInfo = client.groups.getLongPollServer(communityId)
        server = serverInfo.server
        key = serverInfo.key
        ts = serverInfo.ts
    }

    private suspend fun poll() {
        val updates = client.execute(GetUpdatesVkMethod().apply {
            this.key = this@AdvancedLongPollBot.key
            this.server = this@AdvancedLongPollBot.server
            this.ts = this@AdvancedLongPollBot.ts
        }) {}

        handle(updates)
        ts = updates.ts
    }

    private fun handleVkError(t: Throwable): Boolean {
        val json = (t.message?.let { runCatching { VkClient.json.parseToJsonElement(it) }.getOrNull() } as? JsonObject) ?: return false
        val failed = (json["failed"] as? JsonPrimitive)?.intOrNull ?: return false

        return when (failed) {
            1 -> {
                ts = json["ts"]?.jsonPrimitive?.content ?: ""
                true
            }
            2, 3 -> {
                nextCacheReset = Instant.MIN
                true
            }
            else -> false
        }
    }

    protected abstract fun handle(result: GetUpdatesVkMethod.Result)
}


