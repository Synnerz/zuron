package com.github.synnerz.zuron.python

import com.github.synnerz.zuron.Zuron
import org.python.core.PyString
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
            val sys = interpreter.systemState
            val path = PyString(file.parentFile.absolutePath)
            sys.path.insert(0, path)

            interpreter.execfile(file.absolutePath)

            sys.path.remove(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}