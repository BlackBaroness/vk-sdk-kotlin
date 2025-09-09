package io.github.blackbaroness.vk.model.`object`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientInfo(
    @SerialName("button_actions") val buttonActions: List<String>,
    val keyboard: Boolean,
    @SerialName("inline_keyboard") val inlineKeyboard: Boolean,
    val carousel: Boolean,
    @SerialName("lang_id") val langId: Int,
)