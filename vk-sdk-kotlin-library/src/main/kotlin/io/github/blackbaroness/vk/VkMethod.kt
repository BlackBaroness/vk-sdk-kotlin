package io.github.blackbaroness.vk

import io.ktor.http.HttpMethod
import io.ktor.util.reflect.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class VkMethod<RESULT> {
    abstract val name: String
    val parameters: MutableMap<String, Any?> = mutableMapOf()
    abstract val resultTypeInfo: TypeInfo
    abstract val httpMethod: HttpMethod
    open val customUrl: String? = null

    @Suppress("UNCHECKED_CAST")
    protected fun <T> parameter(name: String): ReadWriteProperty<Any?, T?> =
        object : ReadWriteProperty<Any?, T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) = parameters[name] as? T
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) { parameters[name] = value }
        }
}