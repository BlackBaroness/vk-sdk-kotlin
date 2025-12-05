@file:ApiStatus.Internal

package io.github.blackbaroness.vk.model.internal

import org.jetbrains.annotations.ApiStatus

interface ParameterConverter<PUBLIC, PRIVATE> {
    fun toPublic(private: PRIVATE): PUBLIC
    fun toPrivate(public: PUBLIC): PRIVATE
}

object BooleanIntParameterConverter : ParameterConverter<Boolean, Int> {

    override fun toPublic(private: Int) = when (private) {
        0 -> false
        1 -> true
        else -> error("Unsupported boolean/int value: $private")
    }

    override fun toPrivate(public: Boolean) = when (public) {
        false -> 0
        true -> 1
    }
}
