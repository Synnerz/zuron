package com.github.synnerz.zuron.python

import com.github.synnerz.zuron.Zuron
import org.python.util.PythonInterpreter
import java.io.File

object PyLoader {
    lateinit var interpreter: PythonInterpreter

    fun setup() {
        interpreter = PythonInterpreter()
    }

    fun preInit() {}

    fun init() {
        Zuron.foldersIn(Zuron.modulesPy).forEach {
            it.listFiles().forEach { ff ->
                if (ff.nameWithoutExtension == "main" && ff.extension == "py")
                    loadModule(ff)
            }
        }
    }

    fun loadModule(file: File) {
        try {
            val script = interpreter.compile(file.readText(Charsets.UTF_8), file.name)
            interpreter.exec(script)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}