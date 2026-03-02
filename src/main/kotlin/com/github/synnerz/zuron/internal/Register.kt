package com.github.synnerz.zuron.internal

import com.github.synnerz.zuron.js.JSLoader
import com.github.synnerz.zuron.js.JSLoader.wrapInContext
import org.luaj.vm2.LuaClosure
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.mozilla.javascript.Callable
import org.mozilla.javascript.Context
import org.python.core.Py
import org.python.core.PyObject
import java.util.concurrent.ConcurrentHashMap

/**
 * Inspired from ChatTriggers which is under MIT License
 * [Link](https://github.com/ChatTriggers/ctjs/blob/main/src/main/kotlin/com/chattriggers/ctjs/engine/Register.kt)
 */
object Register {
    val registers = ConcurrentHashMap<String, MutableList<Any>>()

    @JvmStatic
    fun register(name: String, method: Any) {
        val type = name.lowercase().trim()
        registers.getOrPut(type, { mutableListOf() }).add(method)
    }

    @JvmStatic
    fun trigger(name: String, vararg args: Any?) {
        val type = name.lowercase().trim()
        val methods = registers[type] ?: return
        methods.forEach { method ->
            when (method) {
                is Callable -> wrapInContext {
                    Context.jsToJava(method.call(it, JSLoader.moduleScope, JSLoader.moduleScope, args), Any::class.java)
                }
                is LuaClosure -> if (method.isfunction())
                    method.invoke(LuaValue.varargsOf(args.map { CoerceJavaToLua.coerce(it) }.toTypedArray()))
                is PyObject -> if (method.isCallable)
                    method.__call__(args.map { Py.java2py(it) }.toTypedArray())

            }
        }
    }
}