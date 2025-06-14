package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Keyboard(
    val buttons: List<List<Button>>,
    @SerialName("one_time") val oneTime: Boolean,
    val inline: Boolean = false,
) {
    @Serializable
    data class Button(
        val action: Action,
        val color: Color? = null,
    ) {

        @Serializable
        data class Action(
            val type: Type,
            val label: String? = null,
            val payload: String? = null,
            val link: String? = null,
            @SerialName("app_id") val appId: Long? = null,
            @SerialName("owner_id") val ownerId: Long? = null,
            val hash: String? = null,
        ) {

            @Serializable
            enum class Type {
                @SerialName("text")
                TEXT,

                @SerialName("open_link")
                OPEN_LINK,

                @SerialName("callback")
                CALLBACK;
            }
        }

        @Serializable
        enum class Color {
            @SerialName("primary")
            PRIMARY,

            @SerialName("secondary")
            SECONDARY,

            @SerialName("negative")
            NEGATIVE,

            @SerialName("positive")
            POSITIVE
        }
    }
}