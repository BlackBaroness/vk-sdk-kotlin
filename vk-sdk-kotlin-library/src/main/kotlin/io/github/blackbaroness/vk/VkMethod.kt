package io.github.blackbaroness.vk

import io.github.blackbaroness.vk.model.`object`.Keyboard
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

    @Suppress("UNCHECKED_CAST")
    protected fun parameterKeyboard(name: String): ReadWriteProperty<Any?, Keyboard?> =
        object : ReadWriteProperty<Any?, Keyboard?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): Keyboard? {
                return parameters[name]?.let { VkClient.json.decodeFromString(it as String) }
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: Keyboard?) {
                parameters[name] = VkClient.json.encodeToString(value)
            }

        }
}
