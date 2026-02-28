package com.github.synnerz.zuron.js

import com.github.synnerz.zuron.internal.ILoader
import com.github.synnerz.zuron.Zuron
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider
import org.mozilla.javascript.commonjs.module.Require
import org.mozilla.javascript.commonjs.module.provider.StrongCachingModuleScriptProvider
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider
import java.io.File
import java.lang.reflect.Method
import java.net.URI
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Taken from ChatTriggers under MIT License
 * [Link](https://github.com/ChatTriggers/ctjs/blob/main/src/main/kotlin/com/chattriggers/ctjs/internal/engine/JSLoader.kt)
 */
object JSLoader : ILoader {
    lateinit var moduleScope: Scriptable
    lateinit var evalScope: Scriptable
    lateinit var requiresScope: CustomRequire
    lateinit var moduleProvider: ModuleScriptProvider

    override fun setup() {
        val ctx = JSContextFactory.enterContext()
        val srcProvider = UrlModuleSourceProvider(listOf(Zuron.modulesJs.toURI()), listOf())
        moduleProvider = StrongCachingModuleScriptProvider(srcProvider)
        moduleScope = ImporterTopLevel(ctx)
        evalScope = ImporterTopLevel(ctx)
        requiresScope = CustomRequire(moduleProvider)
        requiresScope.install(moduleScope)
        requiresScope.install(evalScope)
        Context.exit()
    }

    override fun preInit() {
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

    override fun init() {
        Zuron.foldersIn(Zuron.modulesJs).forEach {
            it.listFiles().forEach { ff ->
                if (ff.nameWithoutExtension == "index" && ff.extension == "js")
                    loadModule(ff)
            }
        }
    }

    override fun loadModule(file: File): Unit = wrapInContext {
        try {
            requiresScope.loadModule(file.parentFile.name, file.toURI())
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

    class CustomRequire(
        moduleProvider: ModuleScriptProvider,
    ) : Require(Context.getCurrentContext(), moduleScope, moduleProvider, null, null, false) {
        // this reflection overhead is reaaal great
        val getExportedModuleInterfaceRefl: Method = Require::class.java.getDeclaredMethod(
            "getExportedModuleInterface",
            Context::class.java,
            String::class.java,
            URI::class.java,
            URI::class.java,
            Boolean::class.java
        ).apply {
            trySetAccessible()
        }

        fun loadModule(cachedName: String, uri: URI): Scriptable {
            return getExportedModuleInterfaceRefl.invoke(this, Context.getCurrentContext(), cachedName, uri, null, false) as Scriptable
        }
    }
}