package io.github.blackbaroness.vk.model.exception

class LongPollVkException(
    val failed: Int,
    val ts: String? = null,
) : Exception("Long Poll failed: $failed")
