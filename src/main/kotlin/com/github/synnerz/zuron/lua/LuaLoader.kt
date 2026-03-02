package com.github.synnerz.zuron.lua

import com.github.synnerz.zuron.internal.ILoader
import com.github.synnerz.zuron.Zuron
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

object LuaLoader : ILoader() {
    lateinit var globals: Globals

    override fun setup() {
        globals = JsePlatform.standardGlobals()
    }

    override fun preInit() {
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

    override fun init() {
        Zuron.foldersIn(Zuron.modulesLua).forEach {
            it.listFiles().forEach { ff ->
                if (ff.nameWithoutExtension == "main" && ff.extension == "lua")
                    loadModule(ff)
            }
        }
    }

    override fun loadModule(file: File) {
        try {
            val folderPath = file.parentFile.absolutePath.replace("\\", "/")
            globals.load("package.path = package.path .. ';' .. '$folderPath/?.lua'").call()
            globals.loadfile(file.absolutePath).call()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}