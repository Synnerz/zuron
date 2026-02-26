package com.github.synnerz.zuron.js

import com.github.synnerz.zuron.Zuron
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider
import org.mozilla.javascript.commonjs.module.provider.StrongCachingModuleScriptProvider
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Taken from ChatTriggers under MIT License
 * [Link](https://github.com/ChatTriggers/ctjs/blob/main/src/main/kotlin/com/chattriggers/ctjs/internal/engine/JSLoader.kt)
 */
object JSLoader {
    lateinit var moduleScope: Scriptable
    lateinit var evalScope: Scriptable
    lateinit var moduleProvider: ModuleScriptProvider

    fun preInit() {
        wrapInContext {
            try {
                val script = it.compileString(
                    "this.Java = { type: (name) => Packages[name] }",
                    "Helper",
                    1, null
                )
                script.exec(it, moduleScope)
                script.exec(it, evalScope)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun init() {
        Zuron.foldersIn(Zuron.modulesJs).forEach {
            // TODO: maybe make better or something
            it.listFiles().forEach { ff ->
                if (ff.isDirectory) ff.listFiles().forEach { ff2 -> if (ff2.extension == "js") loadModule(ff2) }
                if (ff.extension == "js") loadModule(ff)
            }
        }
    }

    fun setup() {
        val ctx = JSContextFactory.enterContext()
        val srcProvider = UrlModuleSourceProvider(listOf(Zuron.modulesJs.toURI()), listOf())
        moduleProvider = StrongCachingModuleScriptProvider(srcProvider)
        moduleScope = ImporterTopLevel(ctx)
        evalScope = ImporterTopLevel(ctx)
        Context.exit()
    }

    fun loadModule(file: File): Unit = wrapInContext {
        try {
            val s = it.compileString(file.readText(Charsets.UTF_8), file.name, 1, null)
            s.exec(it, moduleScope)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalContracts::class)
    internal inline fun <T> wrapInContext(context: Context? = null, crossinline block: (Context) -> T): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }

        var cx = context ?: Context.getCurrentContext()
        val missingContext = cx == null
        if (missingContext)
            cx = JSContextFactory.enterContext()

        try {
            return block(cx)
        } finally {
            if (missingContext) Context.exit()
        }
    }
    // TODO: find out if we actually need the custom requires
}