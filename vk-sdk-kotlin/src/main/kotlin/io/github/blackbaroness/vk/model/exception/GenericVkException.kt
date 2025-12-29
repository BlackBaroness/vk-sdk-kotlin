package io.github.blackbaroness.vk.model.exception

import io.github.blackbaroness.vk.VkMethod
import io.ktor.http.*

class GenericVkException private constructor(
    override val message: String,
    override val cause: Throwable?
) : Exception() {

    constructor(
        code: Int,
        originalMessage: String
    ) : this("$code: $originalMessage", null)

    constructor(
        method: VkMethod<*>,
        url: String?,
        status: HttpStatusCode?,
        answer: String?,
        cause: Throwable?
    ) : this(
        """
        VK API error
        Method: ${method::class.simpleName}
        Url: $url
        Method parameters: ${method.parameters}
        Status: $status
        Answer: $answer
        """.trimIndent(), cause
    )
}
