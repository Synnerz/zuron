package com.github.synnerz.zuron.internal

import com.github.synnerz.zuron.Zuron
import java.io.File

abstract class ILoader {
    init {
        Zuron.loadedEngines.add(this)
    }

    /**
     * - Set up the actual engine
     */
    abstract fun setup()

    /**
     * - Initialize any post engine tasks, for example adding a function to the global scope
     */
    open fun preInit() {}

    /**
     * - Load all the language directory's main files
     */
    abstract fun init()

    /**
     * - Loads a file and executes it instantly
     */
    abstract fun loadModule(file: File)

    /**
     * - Re-builds the engine
     */
    open fun reload() {
        setup()
        preInit()
        init()
    }
}