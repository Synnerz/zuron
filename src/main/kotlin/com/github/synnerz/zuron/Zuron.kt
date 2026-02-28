package com.github.synnerz.zuron

import com.github.synnerz.zuron.js.JSLoader
import com.github.synnerz.zuron.lua.LuaLoader
import com.github.synnerz.zuron.python.PyLoader
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import org.slf4j.LoggerFactory
import java.io.File

object Zuron : ClientModInitializer {
    private val logger = LoggerFactory.getLogger("zuron")
	val configLocation = File("./config")
	val modulesJs = File(configLocation, "zuron/js").apply { mkdirs() }
	val modulesPy = File(configLocation, "zuron/py").apply { mkdirs() }
	val modulesLua = File(configLocation, "zuron/lua").apply { mkdirs() }

	override fun onInitializeClient() {
		// TODO: since py and lua have single global scope
		//  it is possible for global variables to leak to each other
		//  fix this later
		// Why not call ILoader::reload ?
		//  simple, we want these to run as shown in here rather than engine by engine
		JSLoader.setup()
		PyLoader.setup()
		LuaLoader.setup()

		JSLoader.preInit()
		PyLoader.preInit()
		LuaLoader.preInit()

		JSLoader.init()
		PyLoader.init()
		LuaLoader.init()

		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			// TODO:
			//  disable - disables all engines
			//  disable <engine> - disables single engine
			//  reload <engine> - reloads single engine
			//  (might not happen soon) reload <engine> <moduleName> - reloads single module
			val cmd = ClientCommandManager.literal("zr")
				.then(ClientCommandManager.literal("load").executes {
					JSLoader.reload()
					PyLoader.reload()
					LuaLoader.reload()
					1
				})
			dispatcher.register(cmd)
		}
	}

	fun foldersIn(file: File): List<File> {
		if (!file.isDirectory) return emptyList()
		return file.listFiles()?.filter { it.isDirectory } ?: emptyList()
	}
}