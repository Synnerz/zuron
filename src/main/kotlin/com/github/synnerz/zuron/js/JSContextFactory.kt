package com.github.synnerz.zuron.js

import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.WrapFactory
import java.net.URL
import java.net.URLClassLoader

/**
 * Taken from ChatTriggers under MIT License
 * [Link](https://github.com/ChatTriggers/ctjs/blob/main/src/main/kotlin/com/chattriggers/ctjs/internal/engine/JSContextFactory.kt)
 */
object JSContextFactory : ContextFactory() {
    var classLoader = ModifiedURLClassLoader()

    override fun onContextCreated(cx: Context?) {
        super.onContextCreated(cx)
        if (cx == null) return

        cx.applicationClassLoader = classLoader
        cx.languageVersion = Context.VERSION_ECMASCRIPT
        cx.wrapFactory = WrapFactory().apply {
            isJavaPrimitiveWrap = false
        }
    }

    class ModifiedURLClassLoader : URLClassLoader(arrayOf(), javaClass.classLoader) {
        val sources = mutableSetOf<URL>()

        fun addAllURLs(urls: List<URL>) {
            (urls - sources).forEach(::addURL)
        }

        public override fun addURL(url: URL) {
            super.addURL(url)
            sources.add(url)
        }
    }
}