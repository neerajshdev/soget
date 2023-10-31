package com.njsh.reelssaver

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object Executor {
    private val executor = Executors.newCachedThreadPool()

    fun instance(): ExecutorService {
        return executor
    }

    fun execute(task: () -> Unit) {
        executor.execute(task)
    }
}