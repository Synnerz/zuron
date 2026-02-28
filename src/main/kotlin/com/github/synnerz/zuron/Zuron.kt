package com.github.synnerz.zuron

import com.github.synnerz.zuron.js.JSLoader
import com.github.synnerz.zuron.lua.LuaLoader
import com.github.synnerz.zuron.python.PyLoader
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory
import java.io.File

object Zuron : ClientModInitializer {
    private val logger = LoggerFactory.getLogger("zuron")
	val configLocation = File("./config")
	val modulesJs = File(configLocation, "zuron/js").apply { mkdirs() }
	val modulesPy = File(configLocation, "zuron/py").apply { mkdirs() }
	val modulesLua = File(configLocation, "zuron/lua").apply { mkdirs() }

	override fun onInitializeClient() {
		JSLoader.setup()
		PyLoader.setup()
		LuaLoader.setup()

		JSLoader.preInit()
		PyLoader.preInit()
		LuaLoader.preInit()

		JSLoader.init()
		PyLoader.init()
		LuaLoader.init()
	}

	fun foldersIn(file: File): List<File> {
		if (!file.isDirectory) return emptyList()
		return file.listFiles()?.filter { it.isDirectory } ?: emptyList()
	}
}