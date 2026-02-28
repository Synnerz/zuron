package com.github.synnerz.zuron

import java.io.File

interface ILoader {
    /**
     * - Set up the actual engine
     */
    fun setup()

    /**
     * - Initialize any post engine tasks, for example adding a function to the global scope
     */
    fun preInit() {}

    /**
     * - Load all the language directory's main files
     */
    fun init()

    /**
     * - Loads a file and executes it instantly
     */
    fun loadModule(file: File)

    /**
     * - Re-builds the engine
     */
    fun reload() {
        setup()
        preInit()
        init()
    }
}