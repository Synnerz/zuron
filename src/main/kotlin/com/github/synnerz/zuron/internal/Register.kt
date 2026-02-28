package com.github.synnerz.zuron.internal

import java.util.concurrent.ConcurrentHashMap

/**
 * Inspired from ChatTriggers which is under MIT License
 * [Link](https://github.com/ChatTriggers/ctjs/blob/main/src/main/kotlin/com/chattriggers/ctjs/engine/Register.kt)
 */
object Register {
    val customRegisters = ConcurrentHashMap<String, (Array<Any>) -> Unit>()
    val regs = ConcurrentHashMap<String, MutableList<(Array<out Any?>) -> Unit>>()

    @JvmStatic
    fun register(name: String, method: (Array<out Any?>) -> Unit) {
        val type = name.lowercase()
        regs.getOrPut(type, { mutableListOf() }).add(method)
    }

    @JvmStatic
    fun trigger(name: String, vararg args: Any?) {
        regs.forEach { it.value.forEach { it.invoke(args) } }
    }
}