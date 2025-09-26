package io.github.blackbaroness.vk.model.method

import io.github.blackbaroness.vk.VkClient.Companion.json
import io.github.blackbaroness.vk.VkMethod
import io.github.blackbaroness.vk.model.`object`.ClientInfo
import io.github.blackbaroness.vk.model.`object`.Message
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

// https://dev.vk.com/ru/api/bots-long-poll/getting-started
class GetUpdatesVkMethod : VkMethod<GetUpdatesVkMethod.Result>() {

    override val name get() = throw UnsupportedOperationException()
    override val resultSerializer = Result.serializer()
    override val httpMethod = HttpMethod.Get
    override val resultStyle = ResultStyle.CUSTOM

    override val customUrl get() = "$server?act=a_check&key=$key&ts=$ts&wait=$wait"

    lateinit var key: String
    lateinit var server: String
    lateinit var ts: String
    var wait: Int = 25

    @Serializable
    data class Result(
        val ts: String,
        val updates: List<Update>,
    ) {

        @Serializable
        data class Update(
            @SerialName("type") val type: String,
            @SerialName("object") val obj: JsonElement,
            @SerialName("group_id") val groupId: Long,
        ) {

            val objectResolved: UpdateObject? by lazy {
                when (type) {
                    "message_new" -> json.decodeFromJsonElement<UpdateObject.MessageNew>(obj)
                    "message_event" -> json.decodeFromJsonElement<UpdateObject.MessageEvent>(obj)
                    else -> null
                }
            }

            val asMessageNew: UpdateObject.MessageNew? = objectResolved as? UpdateObject.MessageNew

            val asMessageEvent: UpdateObject.MessageEvent? = objectResolved as? UpdateObject.MessageEvent

            @Serializable
            sealed class UpdateObject {

                @Serializable
                data class MessageNew(
                    @SerialName("message") val message: Message,
                    @SerialName("client_info") val clientInfo: ClientInfo,
                ) : UpdateObject()

                // https://dev.vk.com/ru/api/community-events/json-schema#message_event
                @Serializable
                data class MessageEvent(
                    @SerialName("peer_id") val peerId: Long,
                    @SerialName("user_id") val userId: Long,
                    @SerialName("event_id") val eventId: String,
                    @SerialName("payload") @Serializable(with = PayloadAsStringSerializer::class) val payload: String,
                    @SerialName("conversation_message_id") val conversationMessageId: Long?,
                ) : UpdateObject()
            }
        }
    }

    object PayloadAsStringSerializer : KSerializer<String> {
        override val descriptor = PrimitiveSerialDescriptor(this::class.qualifiedName!!, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): String {
            val input = decoder as? JsonDecoder ?: error("This serializer can be used only with Json")
            return when (val element = input.decodeJsonElement()) {
                is JsonPrimitive -> element.content
                is JsonObject, is JsonArray -> element.toString()
            }
        }

        override fun serialize(encoder: Encoder, value: String) {
            encoder.encodeString(value)
        }
    }
}
