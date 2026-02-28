package com.github.synnerz.zuron.python

import com.github.synnerz.zuron.ILoader
import com.github.synnerz.zuron.Zuron
import org.python.core.PyString
import org.python.util.PythonInterpreter
import java.io.File

object PyLoader : ILoader {
    lateinit var interpreter: PythonInterpreter

    override fun setup() {
        interpreter = PythonInterpreter()
    }

    override fun init() {
        Zuron.foldersIn(Zuron.modulesPy).forEach {
            it.listFiles().forEach { ff ->
                if (ff.nameWithoutExtension == "main" && ff.extension == "py")
                    loadModule(ff)
            }
        }
    }

    override fun loadModule(file: File) {
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

    override fun reload() {
        interpreter.close()
        super.reload()
    }
}