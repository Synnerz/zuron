package com.github.synnerz.zuron.lua

import com.github.synnerz.zuron.Zuron
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

object LuaLoader {
    lateinit var globals: Globals

    fun setup() {
        globals = JsePlatform.standardGlobals()
    }

    fun preInit() {
        val table = LuaTable()

        table.set("type", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val luajava = globals.get("luajava")
                val bindClass = luajava.get("bindClass")
                return bindClass.call(arg)
            }
        })

        globals.set("Java", table)
    }

    fun init() {
        Zuron.foldersIn(Zuron.modulesLua).forEach {
            it.listFiles().forEach { ff ->
                if (ff.isDirectory) ff.listFiles().forEach { ff2 -> if (ff2.extension == "lua") loadModule(ff2) }
                if (ff.extension == "lua") loadModule(ff)
            }
        }
    }

    fun loadModule(file: File) {
        try {
            globals.load(file.readText(Charsets.UTF_8), file.name).call()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}