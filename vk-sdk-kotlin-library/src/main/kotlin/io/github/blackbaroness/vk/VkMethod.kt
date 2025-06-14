package io.github.blackbaroness.vk

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class VkMethod<RESULT> {
    abstract val name: String
    val parameters: MutableMap<String, Any?> = mutableMapOf()
    abstract val resultSerializer: KSerializer<RESULT>
    abstract val httpMethod: HttpMethod
    open val customUrl: String? = null

    @Suppress("UNCHECKED_CAST")
    protected fun <T> parameter(name: String): ReadWriteProperty<Any?, T?> =
        object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = parameters[name] as? T
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                parameters[name] = value
            }
        }
}
